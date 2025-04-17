package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class PointFacadeConcurrencyTest {
    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp(){
        pointRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("포인트_충전_동시성_테스트")
    void charge_point_concurrency() throws InterruptedException {
        long userId = 1L;
        long chargeAmount = 100L;
        int threadCnt = 10; // 동시 실행할 쓰레드 수
        Point userPoint = Point.create(userId, 0);
        pointRepository.save(userPoint);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // CountDownLatch
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 시작 시점 동기화
                    pointFacade.chargePoint(userId, chargeAmount);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 테스트 시작
        endLatch.await(); // 모든 쓰레드가 끝날 때까지 대기
        executorService.shutdown();

        // 최종 포인트 확인
        PointCommand result = pointFacade.getUserPoint(userId);
        long expectedPoint = chargeAmount * threadCnt;
        assertThat(result.userPoint()).isEqualTo(expectedPoint);

        // 결제 내역 중복 확인
        List<Payment> payments = paymentRepository.findByUserId(userId);
        assertThat(payments).hasSize(threadCnt); // 결제 내역은 각 쓰레드마다 하나씩 있어야 함
    }

}