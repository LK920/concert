package kr.hhplus.be.server.domain.queue;

import java.util.List;

public interface WaitingQueueRepository {
    void save(WaitingQueue waitingQueue);
    WaitingQueue findByToken(String token);
    long countByStatus(WaitingQueueStatus waitingQueueStatus);

    long countByStatusAndIdLessThan(WaitingQueueStatus waitingQueueStatus, long id);

    List<WaitingQueue> findAllNotExpired();

    void saveAll(List<WaitingQueue> expiredQueue);
}
