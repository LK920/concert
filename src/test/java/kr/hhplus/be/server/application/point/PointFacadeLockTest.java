package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class PointFacadeLockTest {

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private PointRepository pointRepository;


    @Test
    void checkAOPProxy() {
        log.info("is AOP proxy : {}", AopUtils.isAopProxy(pointFacade));
    }

    @Test
    @DisplayName("동시에_N개_의_충전_요청이_들어오면_하나만_성공")
    void pointCharge_success_when_same_time() throws InterruptedException {
        long userId = 1l;
        long amount = 1000l;
        int threadCnt = 15;
        pointRepository.save(Point.create(userId, 0));
        AtomicLong failCnt = new AtomicLong(0);
        AtomicLong successCnt = new AtomicLong(0);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        for(int i = 0; i<threadCnt; i++){
            executorService.submit(()->{
                try{
                    pointFacade.chargePoint(userId, amount);
                    successCnt.getAndIncrement();
                } catch (Exception e) {
                    log.error(e.getMessage());
                    failCnt.getAndIncrement();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

//        then
        Optional<Point> optPoint = pointRepository.findByUserId(userId);

        assertThat(optPoint.get().getPoint()).isEqualTo(amount);
        assertThat(failCnt.get()).isEqualTo(threadCnt - 1);
    }
}
