package kr.hhplus.be.server.infra.queue;

import kr.hhplus.be.server.domain.queue.redis.WaitingQueueStorage;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class RedisWaitingQueueStorage implements WaitingQueueStorage {

    private final RedissonClient redissonClient;

    private static final String WAITING_QUEUE_KEY = "queue:waiting";
    private static final String ACTIVE_LIST_KEY = "queue:active";
    private static final String TOKEN_USER_KEY_PREFIX = "queue:user:";
    private static final String TOKEN_TTL_KEY_PREFIX = "queue:ttl:";
    private static final Duration ACTIVE_DURATION = Duration.ofMinutes(3);

    @Override
    public void enqueue(String token, long userId) {
        RQueue<String> waitingQueue = redissonClient.getQueue(WAITING_QUEUE_KEY); // List에 rpush로 토큰 추가
        RBucket<String> userBucket = redissonClient.getBucket(TOKEN_USER_KEY_PREFIX + token); // String 구조

        waitingQueue.add(token);
        userBucket.set(String.valueOf(userId));
    }

    @Override
    public boolean isActive(String token) {
        return redissonClient.getList(ACTIVE_LIST_KEY).contains(token);
    }

    @Override
    public int getActiveCount() {
        return redissonClient.getList(ACTIVE_LIST_KEY).size();
    }

    @Override
    public int getWaitingNumber(String token) {
        List<Object> queue = redissonClient.getQueue(WAITING_QUEUE_KEY).readAll();
        int index = queue.indexOf(token);
        if (index == -1) {
            throw new IllegalStateException("대기열에 존재하지 않는 토큰입니다: " + token);
        }
        return index + 1;
    }

    @Override
    public long getOldestActiveRemainingMillis(long waitingNumber) {
        List<Object> activeList = redissonClient.getList(ACTIVE_LIST_KEY).readAll();
        if (activeList.isEmpty()) return 0;
        String oldest = (String) activeList.get(0);
        long remainTime = redissonClient.getBucket(TOKEN_TTL_KEY_PREFIX + oldest).remainTimeToLive();
        return waitingNumber * ACTIVE_DURATION.toMinutes() + remainTime;
    }

    @Override
    public void activate(String token) {
        RList<String> activeList = redissonClient.getList(ACTIVE_LIST_KEY);
        RBucket<Boolean> ttlBucket = redissonClient.getBucket(TOKEN_TTL_KEY_PREFIX + token);

        if (!activeList.contains(token)) {
            activeList.add(token);
            ttlBucket.set(true, ACTIVE_DURATION);
        }

        redissonClient.getQueue(WAITING_QUEUE_KEY).remove(token); // 대기열에서 제거
    }

    @Override
    public void expireInactiveTokens() {
        RList<String> activeList = redissonClient.getList(ACTIVE_LIST_KEY);
        for (String token : activeList.readAll()) {
            RBucket<Boolean> bucket = redissonClient.getBucket(TOKEN_TTL_KEY_PREFIX + token);
            if (bucket.remainTimeToLive() <= 0) {
                activeList.remove(token);
                bucket.delete(); // TTL 버킷도 제거
            }
        }
    }

    @Override
    public List<String> activateFromWaiting(int count) {
        RQueue<String> waitingQueue = redissonClient.getQueue(WAITING_QUEUE_KEY);
        List<String> activatedTokens = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String token = waitingQueue.poll(); // 자동 제거
            if (token == null) break;

            activate(token); // TTL 설정 및 ACTIVE 리스트에 추가
            activatedTokens.add(token);
        }
        return activatedTokens;
    }

    @Override
    public long getUserId(String token) {
        RBucket<String> bucket = redissonClient.getBucket(TOKEN_USER_KEY_PREFIX + token);
        String userId = bucket.get();
        if (userId == null) {
            throw new IllegalArgumentException("존재하지 않는 토큰입니다: " + token);
        }
        return Long.parseLong(userId);
    }

    @Override
    public void removeFromActive(String token) {
        redissonClient.getList(ACTIVE_LIST_KEY).remove(token);
        redissonClient.getBucket(TOKEN_TTL_KEY_PREFIX + token).delete();
    }
}
