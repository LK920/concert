package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    @DisplayName("결제 내역 생성 시 결제 금액이 0이하면 예외 처리")
    void invalidAmount() {
        long userId = 1l;
        long amount = 0;
        PaymentType paymentType = PaymentType.USE;
        assertThatThrownBy(()->Payment.create(userId,amount,paymentType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 0보다 커야합니다.");
    }

    @Test
    @DisplayName("결제 타입이 0일 경우 예외")
    void paymentTypeIsNull() {
        long userId = 1l;
        long amount = 50;
        assertThatThrownBy(()->Payment.create(userId,amount,null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 타입은 필수입니다.");
    }
}