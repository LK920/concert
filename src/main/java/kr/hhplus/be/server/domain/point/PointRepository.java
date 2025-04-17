package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.infra.point.PointRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long>, PointRepositoryCustom {

}
