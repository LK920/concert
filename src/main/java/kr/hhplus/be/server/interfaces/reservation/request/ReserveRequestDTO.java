package kr.hhplus.be.server.interfaces.reservation.request;

public record ReserveRequestDTO(
        long userId,
        long seatId,
        long seatPrice
) {
}
