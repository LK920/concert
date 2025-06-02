package kr.hhplus.be.server.domain.payment.events;

public record PaymentCreatedEvent(
        long userId,
        long concertId,
        long reservationId,
        long paymentId
) {
}
