package kr.hhplus.be.server.interfaces.waitingQueue.response;

import kr.hhplus.be.server.domain.queue.WaitingQueueDetail;
import kr.hhplus.be.server.domain.queue.redis.RedisQueueStatusResponse;

public record ResponseQueue(
    String token,
    long remainedMillis,
    String tokenStatus,
    long waitingNumber
) {

    public static ResponseQueue from(WaitingQueueDetail waitingQueueDetail){
        return  new ResponseQueue(waitingQueueDetail.token(), waitingQueueDetail.remainedMillis(), waitingQueueDetail.status().toString(), waitingQueueDetail.waitingNumber());
    }

    public static ResponseQueue fromRedis(RedisQueueStatusResponse statusResponse){
        return new ResponseQueue(statusResponse.token(), statusResponse.remainedMillis(), statusResponse.status().toString(), statusResponse.waitingNumber());
    }
}
