package kr.hhplus.be.server.domain.reservation.events;

public record ReservationFailedEvent(
        long SeatId
) {
}
