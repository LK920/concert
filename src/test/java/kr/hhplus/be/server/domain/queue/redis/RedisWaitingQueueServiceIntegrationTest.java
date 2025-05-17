package kr.hhplus.be.server.domain.queue.redis;

import kr.hhplus.be.server.RedisTestConfig;
import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisWaitingQueueServiceIntegrationTest extends RedisTestConfig {

    @Autowired
    private RedisWaitingQueueService redisWaitingQueueService;

    @Autowired
    private WaitingQueueStorage waitingQueueStorage;

    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    void setUp(){
        redissonClient.getKeys().flushall();
    }

    @Test
    void testRedissonSetAndGet() {
        // given
        String key = "redisson:test:key";
        String value = "hello from redisson";

        RBucket<String> bucket = redissonClient.getBucket(key);

        // when
        bucket.set(value);
        String retrievedValue = bucket.get();

        // then
        assertThat(retrievedValue).isEqualTo(value);
    }

    @Test
    @DisplayName("대기열_조회_활성_토큰_조회")
    void getWaitingQueue_to_activeToken(){
        // given
        long userId = 1l;
        RedisQueueInfo queueInfo = redisWaitingQueueService.createWaitingQueue(userId);

        RedisQueueStatusResponse queueStatusResponse = redisWaitingQueueService.getWaitingQueue(queueInfo.token());

        assertThat(queueStatusResponse.status()).isEqualTo(WaitingQueueStatus.ACTIVE);
        assertThat(queueStatusResponse.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("대기열_조회_대기토큰_조회")
    void getWaitingQueue_to_waitingToken() {
        // given
        for (int i = 1; i <= 3; i++) {
            RedisQueueInfo info = redisWaitingQueueService.createWaitingQueue(i);
            redisWaitingQueueService.getWaitingQueue(info.token());
        }
        RedisQueueInfo waitingUser = redisWaitingQueueService.createWaitingQueue(99L);

        // when
        RedisQueueStatusResponse queueStatusResponse = redisWaitingQueueService.getWaitingQueue(waitingUser.token());

        // then
        assertThat(queueStatusResponse.status()).isEqualTo(WaitingQueueStatus.WAITING);
        assertThat(queueStatusResponse.waitingNumber()).isGreaterThan(0);
        assertThat(queueStatusResponse.remainedMillis()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("빈자리_있을_시_활성_토큰으로")
    void refreshWaitingQueueStatus_availableSlot(){
        // given
        // active Token 3명
        // 라스트 유저 정보
        String token = "";
        for(long i = 1; i <= 3; i++){
            RedisQueueInfo info = redisWaitingQueueService.createWaitingQueue(i);
            token = info.token();
            waitingQueueStorage.activate(token);
        }
        redissonClient.getBucket("queue:ttl:" + token).delete();
        RedisQueueInfo waitingUser = redisWaitingQueueService.createWaitingQueue(100l);
        waitingQueueStorage.expireInactiveTokens();

        // when
        redisWaitingQueueService.refreshWaitingQueueStatus();
        RedisQueueStatusResponse queueStatusResponse = redisWaitingQueueService.getWaitingQueue(waitingUser.token());

        // then
        assertThat(queueStatusResponse.status()).isEqualTo(WaitingQueueStatus.ACTIVE);
    }

}
