package kr.hhplus.be.server.interfaces.reservation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReserveResponseDTO(
        long reservationId,
        long userId,
        long seatId,
        String reservationStatus
) {
}
