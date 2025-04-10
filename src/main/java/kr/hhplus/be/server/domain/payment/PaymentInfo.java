package kr.hhplus.be.server.domain.payment;

public record PaymentInfo(
        long paymentId,
        long userId,
        long amount,
        PaymentType paymentType
) {
    public static PaymentInfo from(Payment payment){
        return new PaymentInfo(payment.getId(), payment.getUserId(), payment.getAmount(), payment.getPaymentType());
    }
}
