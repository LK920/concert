package kr.hhplus.be.server.infra.point;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.QPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Point> findByUserId(long userId) {
        QPoint point = QPoint.point1;
        Point result = queryFactory.selectFrom(point)
                .where(point.userId.eq(userId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Point> findByUserIdForUpdate(long userId) {
        QPoint point = QPoint.point1;
        Point result = queryFactory.selectFrom(point)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .where(point.userId.eq(userId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

}
