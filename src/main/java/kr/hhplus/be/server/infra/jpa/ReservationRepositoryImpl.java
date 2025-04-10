package kr.hhplus.be.server.infra.jpa;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    @Override
    public void save(Reservation reservation) {

    }

    @Override
    public Reservation findByReservationId(long reservationId) {
        return null;
    }

    @Override
    public List<Reservation> findAllByUserId(long userId) {
        return List.of();
    }
}
