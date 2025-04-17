package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.Payment;

import java.util.List;

public interface PaymentRepositoryCustom {
    List<Payment> findByUserId(long userInd);
}
