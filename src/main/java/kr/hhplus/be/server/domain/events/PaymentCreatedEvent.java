package kr.hhplus.be.server.domain.events;

public record PaymentCreatedEvent(
        long userId,
        long concertId,
        long reservationId,
        long paymentId
) {
}
