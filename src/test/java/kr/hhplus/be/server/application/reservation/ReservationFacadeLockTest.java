package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class ReservationFacadeLockTest {

    @Autowired
    private ReservationFacade reservationFacade;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PointService pointService;

    private long seatId;
    private ReservationInfo reservationInfo;
    private ReserveConcertCommand reserveConcertCommand;
    long seatPrice = 1000l;
    @BeforeEach
    void setUp(){
//        유저 준비, 빈 좌석 준비
        long concertDateId = 1l;
        Seat seat = Seat.create(concertDateId, 1l, seatPrice);
        Seat savedSeat = seatRepository.save(seat);
        seatId = savedSeat.getId();
    }

    @Test
    @DisplayName("좌석_동시_예약_신청_시_한_좌석만_성공_나머지_실패")
    void reservationConcert_success() throws InterruptedException {

        int threadCnt = 100;
//        유저 생성
        for(long u = 1; u <= threadCnt; u++){
            long userId = u;
            long userAmount = 1000l;
            pointRepository.save(Point.create(userId,userAmount));
        }

        AtomicLong failCnt = new AtomicLong(0);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);

        for(int i = 0 ; i < threadCnt; i++){
            long newUserId = i + 1;
            executorService.submit(()->{
               try{
                   reserveConcertCommand = new ReserveConcertCommand(newUserId,seatId, seatPrice);
                   reservationInfo = reservationFacade.reserveConcert(reserveConcertCommand);
               }catch (Exception e){
                   failCnt.getAndIncrement();
               }finally {
                   countDownLatch.countDown();
               }
            });
        }
        countDownLatch.await();
        executorService.shutdown();

        // 좌석 상태 변경확인
        Seat seat = seatRepository.findBySeatId(seatId);
        assertEquals(SeatStatus.DISABLE, seat.getSeatStatus());
        // 예약 생성 및 결제 아이디 추가 확인
        // 포인트 차감 확인
        List<ReservationInfo> reservationInfos =  reservationService.getUserReservationInfo(reservationInfo.userId());
        List<ReservationInfo> filteredInfos = reservationInfos.stream().filter(info->info.seatId() == seatId).collect(Collectors.toList());
        assertThat(filteredInfos.size()).isEqualTo(1);
        assertThat(filteredInfos.get(0).paymentId()).isNotNull();
        Long paymentId = filteredInfos.get(0).paymentId();
        List<PaymentInfo> paymentInfos = paymentService.getUserPaymentList(reservationInfo.userId());
        List<PaymentInfo> filteredPayments = paymentInfos.stream().filter(payment -> payment.paymentId() == (long) paymentId).collect(Collectors.toList());
        assertEquals(1, filteredPayments.size());
        assertEquals(PaymentType.USE, filteredPayments.get(0).paymentType());
        assertEquals(seat.getConcertSeatPrice(), filteredPayments.get(0).amount());
        // 결제 내역 확인
        PointInfo pointInfo =  pointService.getUserPoint(reservationInfo.userId());
        assertEquals(0, pointInfo.userPoint());
        assertThat(failCnt.get()).isEqualTo(99l);
    }

}
