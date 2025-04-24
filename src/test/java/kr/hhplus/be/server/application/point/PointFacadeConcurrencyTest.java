package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.payment.Payment;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

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
        int threadCnt = 1000; // 동시 실행할 쓰레드 수
        AtomicLong successCnt = new AtomicLong();
        AtomicLong failCnt = new AtomicLong();
        Point userPoint = Point.create(userId, 0);
        pointRepository.save(userPoint);
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            executorService.submit(() -> {
                try {
                    pointFacade.chargePoint(userId, chargeAmount);
                    successCnt.getAndIncrement();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    failCnt.getAndIncrement();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 테스트 시작
        endLatch.await(); // 모든 쓰레드가 끝날 때까지 대기
        executorService.shutdown();

        log.info("성공 요청 수: {}", successCnt.get());
        log.info("실패 요청 수: {}", failCnt.get());

        // 최종 포인트 확인
        PointCommand result = pointFacade.getUserPoint(userId);
        long expectedPoint = chargeAmount * successCnt.get();
        assertThat(result.userPoint()).isEqualTo(expectedPoint);

        // 결제 내역 중복 확인
        List<Payment> payments = paymentRepository.findByUserId(userId);
        assertThat(payments).hasSize((int) successCnt.get()); // 결제 내역은 각 쓰레드마다 하나씩 있어야 함
    }
}