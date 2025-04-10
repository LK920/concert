package kr.hhplus.be.server.domain.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    // 결제 내역 생성
    @Transactional
    public PaymentInfo createPayment(long userId, long amount, PaymentType paymentType){
        Payment payment = Payment.create(userId, amount,paymentType);
        paymentRepository.save(payment);
        return PaymentInfo.from(payment);
    }
    // 결제 내역 조회
    @Transactional(readOnly = true)
    public List<PaymentInfo> getUserPaymentList(long userId){
        List<Payment> paymentList = paymentRepository.findByUserId(userId);
        return paymentList.stream().map(
                payment -> PaymentInfo.from(payment)
        ).toList();
    }

}
