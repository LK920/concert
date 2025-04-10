package kr.hhplus.be.server.infra.jpa;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PointRepositoryImpl implements PointRepository {

    @Override
    public Optional<Point> findByUserId(long userId) {
        return Optional.empty();
    }

    @Override
    public void save(Point point) {

    }
}
