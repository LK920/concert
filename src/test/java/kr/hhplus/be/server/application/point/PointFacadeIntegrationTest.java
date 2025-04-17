package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PointFacadeIntegrationTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PointRepository pointRepository;

    @BeforeEach
    void setUp(){
        pointRepository.deleteAll();
    }

    @Test
    @DisplayName("포인트_충전")
    void chargePoint() {
        long userId = 1l;
        long amount = 2000l;
        Point point = Point.create(userId, 0);
        pointRepository.save(point);

        PointCommand command = pointFacade.chargePoint(userId,amount);

        List<PaymentInfo> infos = paymentService.getUserPaymentList(userId);

        assertThat(command).isNotNull();
        assertThat(command.userPoint()).isEqualTo(amount);
        assertThat(command.userId()).isEqualTo(userId);

        assertThat(infos).hasSize(1);
        assertThat(infos.get(0).amount()).isEqualTo(amount);
        assertThat(infos.get(0).paymentType()).isEqualTo(PaymentType.CHARGE);
    }

    @Test
    @DisplayName("포인트_내역_조회")
    void getUserPoint() {
        long userId = 1l;
        long charge = 1000;
        Point point = Point.create(userId,charge);
        pointRepository.save(point);

        PointCommand pointCommand = pointFacade.getUserPoint(userId);

        assertThat(pointCommand).isNotNull();
        assertThat(pointCommand.userId()).isEqualTo(userId);
        assertThat(pointCommand.userPoint()).isEqualTo(charge);

    }
}