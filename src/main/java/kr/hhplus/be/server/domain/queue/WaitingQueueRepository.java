package kr.hhplus.be.server.domain.queue;

public interface WaitingQueueRepository {
    void save(WaitingQueue waitingQueue);
    WaitingQueue findByToken(String token);
    long countByStatus(WaitingQueueStatus waitingQueueStatus);

    long countByStatusAndIdLessThan(WaitingQueueStatus waitingQueueStatus, long id);
}
