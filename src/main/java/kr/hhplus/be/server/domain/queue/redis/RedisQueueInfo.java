package kr.hhplus.be.server.domain.queue.redis;

import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;

import java.time.LocalDateTime;

public record RedisQueueInfo(
        String token,
        long userId,
        WaitingQueueStatus status,
        LocalDateTime expiredAt
) {
    public static RedisQueueInfo waiting(String token, long userId) {
        return new RedisQueueInfo(token, userId, WaitingQueueStatus.WAITING, null);
    }

    public static RedisQueueInfo active(String token, long userId, LocalDateTime expiredAt) {
        return new RedisQueueInfo(token, userId, WaitingQueueStatus.ACTIVE, expiredAt);
    }

    public static RedisQueueInfo expired(String token, long userId) {
        return new RedisQueueInfo(token, userId, WaitingQueueStatus.EXPIRED, null);
    }
}
