package kr.hhplus.be.server.infra.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;

import java.util.List;

public interface ReservationRepositoryCustom {
    Reservation findByReservationId(long reservationId);
    List<Reservation> getUserReservationList(long userId);
}
