package kr.hhplus.be.server.infra.queue;

import kr.hhplus.be.server.domain.queue.WaitingQueue;
import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;

import java.util.List;
import java.util.Optional;

public interface WaitingQueueRepositoryCustom {
    Optional<WaitingQueue> findByToken(String token);
    List<WaitingQueue> getWaitingQueueStatus(WaitingQueueStatus waitingQueueStatus) ;
    long findWaitingNumber(String token);
    Optional<WaitingQueue> findOldestActiveWaiting();
    List<WaitingQueue> findByStatus(WaitingQueueStatus waitingQueueStatus);

    List<WaitingQueue> findOldestWaiting(int availableSlots);
}
