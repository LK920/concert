package kr.hhplus.be.server.domain.events;

public record ReservationFailedEvent(
        long SeatId
) {
}
