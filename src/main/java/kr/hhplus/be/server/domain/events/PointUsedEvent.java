package kr.hhplus.be.server.domain.events;

public record PointUsedEvent(
        long userId,
        long reservationId,
        long concertId,
        long seatId,
        long seatPrice
) {
}
