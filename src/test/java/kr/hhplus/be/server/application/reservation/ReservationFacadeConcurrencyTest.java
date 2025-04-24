package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
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


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class ReservationFacadeConcurrencyTest {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationFacade reservationFacade;
    @Autowired
    private PointService pointService;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp(){
        seatRepository.deleteAll();
        reservationRepository.deleteAll();
        pointRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    @DisplayName("좌석_예약_동시성_테스트")
    void reserve_concert_concurrency() throws InterruptedException {
        // given
        int threadCnt = 500;
        long seatPrice = 1000l;

        Seat seat = Seat.create(1l, 1l, seatPrice);
        Seat savedSeat = seatRepository.save(seat);
        for(long i = 1; i <= threadCnt; i++){
            Point point = Point.create(i, seatPrice);
            pointRepository.save(point);
        }

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCnt);

        for(int j = 0; j < threadCnt; j++){
            long userId = j + 1;
            executorService.submit(()->{
                try {
                    startLatch.await();
                    ReserveConcertCommand command = new ReserveConcertCommand(userId, savedSeat.getId(), seatPrice);
                    reservationFacade.reserveConcert(command);
                } catch (Exception e) {
                    log.error("예약 실패 : {}",e.getMessage());
                }finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        // then
        Optional<Seat> updatedSeat = seatRepository.findById(savedSeat.getId());
        assertThat(updatedSeat.get().getSeatStatus()).isEqualTo(SeatStatus.DISABLE);
        List<Reservation> reservations = reservationRepository.getReservationBySeatId(updatedSeat.get().getId());
        assertThat(reservations).hasSize(1);
        PointInfo userPoint = pointService.getUserPoint(reservations.get(0).getUserId());
        // 좌석은 1개만 예약되어야 함
        // 예약 내역 상태는 complete
        assertThat(reservations.get(0).getReservationStatus()).isEqualTo(ReservationStatus.COMPLETE);
        // 포인트 0이어야함
        assertThat(userPoint.userPoint()).isZero();
        // 결제 내역 상태는 complete
        Optional<Payment> payment = paymentRepository.findById(reservations.get(0).getPaymentId());
        assertThat(payment.get().getPaymentType()).isEqualTo(PaymentType.USE);
        assertThat(payment.get().getAmount()).isEqualTo(seatPrice);
    }

}