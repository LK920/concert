package kr.hhplus.be.server.interfaces.waitingQueue.response;

import kr.hhplus.be.server.domain.queue.WaitingQueueDetail;

public record ResponseQueue(
    String token,
    long remainedMillis,
    String tokenStatus,
    long waitingNumber
) {

    public static ResponseQueue from(WaitingQueueDetail waitingQueueDetail){
        return  new ResponseQueue(waitingQueueDetail.token(), waitingQueueDetail.remainedMillis(), waitingQueueDetail.token(), waitingQueueDetail.waitingNumber());
    }
}
