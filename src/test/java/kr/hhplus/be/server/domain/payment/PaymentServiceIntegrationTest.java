package kr.hhplus.be.server.domain.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class PaymentServiceIntegrationTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp(){
        paymentRepository.deleteAll();
        Payment p1 = Payment.create(1l, 2000, PaymentType.CHARGE);
        Payment p2 = Payment.create(1l, 1000, PaymentType.USE);
        Payment p3 = Payment.create(2l, 2000, PaymentType.CHARGE);
        Payment p4 = Payment.create(3l, 4000, PaymentType.USE);
        paymentRepository.saveAll(List.of(p1,p2,p3,p4));
    }

    @Test
    @DisplayName("결제_생성시_금액이_0이하_예외_발생")
    void createPayment_fail() {
        long userId = 1;
        long amount = 0;
        PaymentType paymentType = PaymentType.USE;

        assertThatThrownBy(()-> paymentService.createPayment(userId,amount,paymentType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("결제 금액은 0보다 커야합니다.");
    }

    @Test
    @DisplayName("결제_내역_생성_성공")
    void createPayment_success() {
        long userId = 1;
        long amount = 2000;
        PaymentType paymentType = PaymentType.USE;

        PaymentInfo saved = paymentService.createPayment(userId,amount,paymentType);

        assertThat(saved).isNotNull();
        assertThat(saved.paymentType()).isEqualTo(paymentType);
        assertThat(saved.userId()).isEqualTo(userId);
        assertThat(saved.amount()).isEqualTo(amount);
    }

    @Test
    @DisplayName("결제_내역이_없는_유저_조회")
    void getUserPaymentList_fail_whenNoPayment() {
        long unknownId = 999l;

        List<PaymentInfo> result = paymentService.getUserPaymentList(unknownId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("결제_내역이_있는_유저_조회")
    void getUserPaymentList_success(){
        long userId = 1l;

        List<PaymentInfo> result = paymentService.getUserPaymentList(userId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(PaymentInfo::userId, PaymentInfo::amount, PaymentInfo::paymentType)
                .contains(
                        tuple(userId,2000l,PaymentType.CHARGE),
                        tuple(userId,1000l,PaymentType.USE)
                );
    }
}
