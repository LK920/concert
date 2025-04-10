package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(long userId);
    void save(Point point);
}
