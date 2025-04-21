package kr.hhplus.be.server.infra.queue;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.queue.QWaitingQueue;
import kr.hhplus.be.server.domain.queue.WaitingQueue;
import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WaitingQueueRepositoryImpl implements WaitingQueueRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<WaitingQueue> findByToken(String token) {
        QWaitingQueue waitingQueue = QWaitingQueue.waitingQueue;
        WaitingQueue result = queryFactory
                .selectFrom(waitingQueue)
                .where(
                        waitingQueue.token.eq(token)
                ).fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public long countByStatus(WaitingQueueStatus waitingQueueStatus) {
        QWaitingQueue waitingQueue = QWaitingQueue.waitingQueue;
        return queryFactory
                .selectFrom(waitingQueue)
                .where(
                        waitingQueue.status.eq(waitingQueueStatus)
                ).stream().count();
    }

    @Override
    public long findWaitingNumber(String token) {
        QWaitingQueue waitingQueue = QWaitingQueue.waitingQueue;
        LocalDateTime createdAt = queryFactory
                .select(waitingQueue.createdAt)
                .from(waitingQueue)
                .where(waitingQueue.token.eq(token))
                .fetchOne();

        long count = queryFactory
                .select(waitingQueue.count())
                .from(waitingQueue)
                .where(
                        waitingQueue.status.eq(WaitingQueueStatus.WAITING),
                        waitingQueue.createdAt.lt(createdAt))
                .fetchOne();
        return count + 1;
    }

    @Override
    public Optional<WaitingQueue> findOldestActiveWaiting() {
        QWaitingQueue waitingQueue = QWaitingQueue.waitingQueue;
        WaitingQueue result = queryFactory.selectFrom(waitingQueue)
                .where(waitingQueue.status.eq(WaitingQueueStatus.ACTIVE))
                .orderBy(waitingQueue.createdAt.asc())
                .limit(1)
                .fetchOne();
        return Optional.of(result);
    }

    @Override
    public List<WaitingQueue> findByStatus(WaitingQueueStatus waitingQueueStatus) {
        QWaitingQueue waitingQueue = QWaitingQueue.waitingQueue;
        return queryFactory.selectFrom(waitingQueue)
                .where(waitingQueue.status.eq(waitingQueueStatus))
                .fetch();
    }

    @Override
    public List<WaitingQueue> findOldestWaiting(int availableSlots) {
        QWaitingQueue waitingQueue = QWaitingQueue.waitingQueue;
        return queryFactory.selectFrom(waitingQueue)
                .where(waitingQueue.status.eq(WaitingQueueStatus.WAITING))
                .orderBy(waitingQueue.createdAt.asc())
                .limit(availableSlots)
                .fetch();
    }
}
