package kr.hhplus.be.server.infra.jpa;

import kr.hhplus.be.server.domain.queue.WaitingQueue;
import kr.hhplus.be.server.domain.queue.WaitingQueueRepository;
import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WaitingQueueRepositoryImpl implements WaitingQueueRepository {
    @Override
    public void save(WaitingQueue waitingQueue) {

    }

    @Override
    public WaitingQueue findByToken(String token) {
        return null;
    }

    @Override
    public long countByStatus(WaitingQueueStatus waitingQueueStatus) {
        return 0;
    }

    @Override
    public long countByStatusAndIdLessThan(WaitingQueueStatus waitingQueueStatus, long id) {
        return 0;
    }

    @Override
    public List<WaitingQueue> findAllNotExpired() {
        return List.of();
    }

    @Override
    public void saveAll(List<WaitingQueue> expiredQueue) {

    }
}
