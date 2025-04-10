package kr.hhplus.be.server.domain.reservation;

import java.util.List;

public interface ReservationRepository {
    void save(Reservation reservation);
    Reservation findByReservationId(long reservationId);
    List<Reservation> findAllByUserId(long userId);
}
