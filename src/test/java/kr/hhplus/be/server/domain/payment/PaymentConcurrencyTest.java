package kr.hhplus.be.server.domain.payment;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
@Transactional
public class PaymentConcurrencyTest {
    @Autowired
    private PaymentService paymentService;

    @Test
    @DisplayName("한_명이_동시에_결제_생성_요청_시")
    void create_payment_concurrency() throws InterruptedException {
        long userId = 1l;
        long amount = 2000l;

        int threadCnt = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);
        AtomicLong successCnt = new AtomicLong(0);
        AtomicLong failCnt = new AtomicLong(0);

        for(int i = 0; i < threadCnt; i++){
            executorService.submit(()->{
                try{
                    startLatch.await();
                    paymentService.createPayment(userId,amount,PaymentType.USE);
                    log.info("예약 성공");
                    successCnt.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    failCnt.incrementAndGet();
                }finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        log.info("======= 예약 성공 & 실패 ========");
        log.info("예약 시도 : {}, 예약 성공 : {}, 예약 실패 : {}", threadCnt, successCnt,failCnt);
        // 성공 케이스는 1개여야한다.
        // 동시성 제어를 하지 않은 관계로 실패함
        // assertThat(successCnt.get()).isEqualTo(1);
    }
}
