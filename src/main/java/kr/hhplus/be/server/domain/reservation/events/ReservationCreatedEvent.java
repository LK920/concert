package kr.hhplus.be.server.domain.reservation.events;

public record ReservationCreatedEvent(
        long userId,
        long concertId,
        long reservationId,
        long seatId,
        long seatPrice
) {
}
