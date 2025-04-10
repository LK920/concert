package kr.hhplus.be.server.infra.jpa;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {
    @Override
    public void save(Payment payment) {

    }

    @Override
    public List<Payment> findByUserId(long userInd) {
        return List.of();
    }
}
