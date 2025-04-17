package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.infra.seat.SeatRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long>, SeatRepositoryCustom {

}
