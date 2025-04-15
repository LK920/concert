package kr.hhplus.be.server.domain.queue;

import java.time.LocalDateTime;

public record WaitingQueueDetail(
        long userId,
        long waitingQueueId,
        long waitingNumber,
        String token,
        WaitingQueueStatus status,
        long remainedMillis
) {
    public static WaitingQueueDetail from(WaitingQueue waitingQueue, long waitingNumb, long remainedMillis){
        return new WaitingQueueDetail(
                waitingQueue.getUserId(),
                waitingQueue.getId(),
                waitingNumb,
                waitingQueue.getToken(),
                waitingQueue.getStatus(),
                remainedMillis);
    }
}
