package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.Point;

import java.util.Optional;

public interface PointRepositoryCustom {
    Optional<Point> findByUserId(long userId);
}
