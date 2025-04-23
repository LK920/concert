package kr.hhplus.be.server.domain.point;

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
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class PointConcurrencyTest {
    @Autowired
    private PointService pointService;
    @Autowired
    private PointRepository pointRepository;

    @BeforeEach
    void setUp(){
        pointRepository.deleteAll();
    }

    @Test
    @DisplayName("한_명이_동시에_충전_요청_시")
    void charge_concurrency() throws InterruptedException {
        long userId = 1l;
        long initPoint = 0;
        long chargePoint = 100l;
        int threadCnt = 1000;
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        Point userPoint = Point.create(userId,initPoint);
        pointRepository.save(userPoint);

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);

        for(int i = 0; i<threadCnt; i++){
            executorService.submit(()->{
               try {
                   startLatch.await();
                   long startTime = System.currentTimeMillis();
                   pointService.chargeUserPoint(userId, chargePoint);
                   long endTime = System.currentTimeMillis();
                   responseTimes.add(endTime - startTime);
                   success.getAndIncrement();
                   log.info("충전 성공");
               } catch (Exception e) {
                   log.error(e.getMessage());
                   fail.getAndIncrement();
               } finally {
                   endLatch.countDown();
               }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        log.info("성공 요청 수: {}", success);
        log.info("실패 요청 수: {}", fail);
        log.info("평균 응답 시간: {} ms", responseTimes.stream().mapToLong(Long::longValue).average().orElse(0));
        log.info("최대 응답 시간: {} ms", responseTimes.stream().mapToLong(Long::longValue).max().orElse(0));
        log.info("최소 응답 시간: {} ms", responseTimes.stream().mapToLong(Long::longValue).min().orElse(0));

        PointInfo pointInfo = pointService.getUserPoint(userId);
        long latestPoint = chargePoint * success.get();
        assertThat(pointInfo.userPoint()).isEqualTo(latestPoint);
    }

    @Test
    @DisplayName("한_명이_동시에_사용_요청_시")
    void use_concurrency() throws InterruptedException {
        long userId = 1l;
        long initPoint = 10000l;
        long usePoint = 100l;
        int threadCnt = 10;
        Point userPoint = Point.create(userId,initPoint);
        pointRepository.save(userPoint);

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);

        for(int i = 0; i<threadCnt; i++){
            executorService.submit(()->{
                try {
                    startLatch.await();
                    pointService.useUserPoint(userId, usePoint);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        PointInfo pointInfo = pointService.getUserPoint(userId);
        long totalUsePoint = usePoint * threadCnt;
        assertThat(pointInfo.userPoint()).isEqualTo(initPoint - totalUsePoint);
    }
}
