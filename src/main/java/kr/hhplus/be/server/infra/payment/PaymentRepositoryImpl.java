package kr.hhplus.be.server.infra.payment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.QPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Payment> findByUserId(long userId) {
        QPayment payment = QPayment.payment;
        List<Payment> result = queryFactory.selectFrom(payment)
                .where(
                        payment.userId.eq(userId)
                )
                .fetch();
        return result;
    }
}
