package kr.hhplus.be.server.domain.queue;

import java.time.LocalDateTime;

public record WaitingQueueInfo(
        String token,
        long userId,
        LocalDateTime expiredAt,
        WaitingQueueStatus status
) {

    public static WaitingQueueInfo from(WaitingQueue waitingQueue){
        return new WaitingQueueInfo(waitingQueue.getToken(), waitingQueue.getUserId(), waitingQueue.getExpiredAt(), waitingQueue.getStatus());
    }

}
