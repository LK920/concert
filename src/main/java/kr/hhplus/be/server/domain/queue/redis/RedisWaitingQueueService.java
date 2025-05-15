package kr.hhplus.be.server.domain.queue.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisWaitingQueueService {

    private static final int MAX_ACTIVE_USERS = 3;

    private final WaitingQueueStorage waitingQueueStorage;

    public RedisQueueInfo createWaitingQueue(long userId) {
        String token = UUID.randomUUID().toString();
        waitingQueueStorage.enqueue(token, userId);
        return RedisQueueInfo.waiting(token, userId);
    }

    public RedisQueueStatusResponse getWaitingQueue(String token) {
        long userId = waitingQueueStorage.getUserId(token);

        // 이미 ACTIVE 상태인지 확인 활성 토큰이면 -> 상태값 리턴
        if (waitingQueueStorage.isActive(token)) {
            return RedisQueueStatusResponse.active(token, userId);
        }

        int activeCount = waitingQueueStorage.getActiveCount(); //활성된 토큰 갯수 조회

        // 빈 자리가 있다면 ACTIVE 처리
        if (activeCount < MAX_ACTIVE_USERS) {
            waitingQueueStorage.activate(token); // 활성 큐에 저장, 대기열 큐에선 제외
            return RedisQueueStatusResponse.active(token, userId);
        }

        // WAITING 상태인 경우, 대기 순번 및 남은 시간 반환
        long waitingNumber = waitingQueueStorage.getWaitingNumber(token); //대기열 큐에서 순서 리턴
        long remainedMillis = waitingQueueStorage.getOldestActiveRemainingMillis(); // 가장 먼저 만료되는 활성 토큰 남은 시간
        return RedisQueueStatusResponse.waiting(token, userId, waitingNumber, remainedMillis); //대기번호, 남은시간, 정보 리턴
    }

    public void refreshWaitingQueueStatus() {
        waitingQueueStorage.expireInactiveTokens();
        int available = MAX_ACTIVE_USERS - waitingQueueStorage.getActiveCount();
        if (available <= 0) return;
        waitingQueueStorage.activateFromWaiting(available);
    }
}

