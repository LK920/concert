package kr.hhplus.be.server.domain.events;

public record PointUsingFailedEvent(
        Long userId,
        Long concertId,
        Long reservationId,
        Long seatId,
        String reason // 실패 사유
) {
}
