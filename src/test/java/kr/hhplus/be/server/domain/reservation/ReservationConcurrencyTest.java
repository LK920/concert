package kr.hhplus.be.server.domain.reservation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("한_아이디로_예약_등록_동시_진행_시")
    void reserve_concurrency() throws InterruptedException {
        long userId = 1l;
        long seatId = 1l;

        int threadCnt = 100; // 총 요청수
        // 비동기로 작업을 수행할 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 모든 스레드가 시작할 때까지 대기시키는 latch 생성
        CountDownLatch startLatch = new CountDownLatch(1);
        // 모든 스레드가 도착할 때까지 대시시키는 latch 생성
        CountDownLatch endLatch = new CountDownLatch(threadCnt);
        AtomicLong successCount = new AtomicLong();
        AtomicLong failCount = new AtomicLong();

        for(int i = 0; i < threadCnt; i++){
            // 쓰레드 등록
            executorService.submit(()->{
                try{
                    startLatch.await(); // 시작 줄에 대기
                    reservationService.createReservation(seatId,userId);
                    log.info("예약성공");
                    successCount.getAndIncrement();
                }catch(Exception e){
                    failCount.getAndIncrement();
                    log.error("예약 실패 : ",e.getMessage());
                }finally {
                    // 이 스레드의 잡업이 끝났음을 알림
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 동시 시작
        endLatch.await();       // 모든 스레드 도착할때까지 대기
        executorService.shutdown();

        log.info("예약 성공 : {}", successCount);
        log.info("예약 실패 : {}", failCount);

        // 계속해서 2개가 성공하는 경우가 나오는데 왜 이런가..?
        // Race condition이 발생 -> db에서 트랜잭션 커밋 타이밍에 충돌 감지 -> 2개까지 허용되는 경우
    }

    @Test
    @DisplayName("여러_아이디가_동시에_한_좌석_예약_진행")
    void reserve_concurrency_multi_user() throws InterruptedException {
        int maxUser = 100; // 총 동시에 접근할 유저 수
        int threadCnt = 100;
        long seatId = 1l;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);
        AtomicLong successCount = new AtomicLong(0);
        AtomicLong failCount = new AtomicLong(0);

        for(int i = 0; i<maxUser; i++){
            long userId = 1 + i;
            executorService.submit(()->{
                try {
                    startLatch.await();
                    reservationService.createReservation(seatId, userId);
                    successCount.incrementAndGet();
                    log.info("예약 성공, 좌석 : {}, 유저 {}", seatId, userId);
                }catch (Exception e){
                    failCount.incrementAndGet();
                    log.error(e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        // 한 좌석만 예약이 되어야하는데 이는 2좌석이 예약되어버린다...
        log.info("======= 예약 성공 & 결과 =======");
        log.info("예약 성공 : {}, 예약 실패 : {}", successCount, failCount);
    }

}
