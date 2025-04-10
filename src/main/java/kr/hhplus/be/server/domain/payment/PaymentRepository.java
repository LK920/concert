package kr.hhplus.be.server.domain.payment;


import java.util.List;

public interface PaymentRepository {
    void save(Payment payment);
    List<Payment> findByUserId(long userInd);
}
