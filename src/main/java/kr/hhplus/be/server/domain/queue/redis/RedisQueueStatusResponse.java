package kr.hhplus.be.server.domain.queue.redis;

import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;

public record RedisQueueStatusResponse(
        String token,
        long userId,
        WaitingQueueStatus status,
        long waitingNumber,
        long remainedMillis
) {
    public static RedisQueueStatusResponse active(String token, long userId) {
        return new RedisQueueStatusResponse(token, userId, WaitingQueueStatus.ACTIVE, 0, 0);
    }

    public static RedisQueueStatusResponse waiting(String token, long userId, long waitingNumber, long remainedMillis) {
        return new RedisQueueStatusResponse(token, userId, WaitingQueueStatus.WAITING, waitingNumber, remainedMillis);
    }

    public static RedisQueueStatusResponse expired(String token, long userId) {
        return new RedisQueueStatusResponse(token, userId, WaitingQueueStatus.EXPIRED, 0, 0);
    }
}
