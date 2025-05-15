package kr.hhplus.be.server.interfaces.reservation.request;

public record ReserveRequestDTO(
        long concertId,
        long userId,
        long seatId,
        long seatPrice
) {
}
