package kr.hhplus.be.server.interfaces.point.request;

public record PointReservationRequest(
        long reservationId,
        long userId
) {
}
