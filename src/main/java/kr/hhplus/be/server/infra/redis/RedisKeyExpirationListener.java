package kr.hhplus.be.server.infra.redis;

import jakarta.annotation.PostConstruct;
import kr.hhplus.be.server.infra.queue.RedisWaitingQueueStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisKeyExpirationListener {

    private final RedissonClient redissonClient;
    private final RedisWaitingQueueStorage waitingQueueStorage;

    private static final String TOKEN_TTL_KEY_PREFIX = "queue:ttl:";

    @PostConstruct
    public void init() {
        redissonClient.getTopic("__keyevent@0__:expired")
                .addListener(String.class, (channel, expiredKey) -> {
                    if (expiredKey.startsWith(TOKEN_TTL_KEY_PREFIX)) {
                        String token = expiredKey.substring(TOKEN_TTL_KEY_PREFIX.length());
                        handleExpiredToken(token);
                    }
                });

        log.info("Redis expiration listener 등록됨");
    }

    private void handleExpiredToken(String token) {
        log.info("만료된 토큰 처리: {}", token);
        waitingQueueStorage.removeFromActive(token); // 아래에 정의
        waitingQueueStorage.activateFromWaiting(1);  // 하나만 ACTIVE로 승격
    }
}
