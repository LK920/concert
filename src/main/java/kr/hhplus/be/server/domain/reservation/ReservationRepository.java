package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.infra.reservation.ReservationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

}
