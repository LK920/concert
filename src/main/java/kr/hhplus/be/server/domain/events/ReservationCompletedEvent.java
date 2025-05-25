package kr.hhplus.be.server.domain.events;

public record ReservationCompletedEvent(
        long userId,
        long reservationId,
        long concertId,
        long seatId
) {
}
