package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.*;
import kr.hhplus.be.server.domain.reservation.*;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReservationFacadeIntegrationTest {
    @Autowired
    private ReservationFacade reservationFacade;
    @Autowired
    private PointService pointService;
    @Autowired
    private SeatService seatService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PointRepository pointRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Seat savedSeat;

    @BeforeEach
    void setUp() {
        pointRepository.deleteAll();
        seatRepository.deleteAll();
        paymentRepository.deleteAll();

        Point point = Point.create(1l,2000);
        Seat seat = Seat.create(1,1,3000);
        pointRepository.save(point);
        savedSeat = seatRepository.save(seat);
    }

    @Test
    @DisplayName("콘서트_예약_잔액_부족_예약_내역만_생성")
    void reserveConcert_insufficient() {
        long userId = 1l;
        long concertId = 1l;
        ReserveConcertCommand command = new ReserveConcertCommand(concertId, userId, savedSeat.getId(), savedSeat.getConcertSeatPrice());

        ReservationInfo info = reservationFacade.reserveConcert(command);

        List<PaymentInfo> paymentList = paymentService.getUserPaymentList(userId);

        assertThat(paymentList).isEmpty();
        assertThat(info.reservationStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(info.paymentId()).isNull();
    }

    @Test
    @DisplayName("콘서트_예약")
    void reserveConcert() throws InterruptedException {
        long userId = 1l;
        long chargePoint = 3000l;
        long concertId = 2l;
        pointService.chargeUserPoint(userId,chargePoint);

        ReserveConcertCommand command = new ReserveConcertCommand(concertId, userId, savedSeat.getId(), savedSeat.getConcertSeatPrice());

        ReservationInfo saved = reservationFacade.reserveConcert(command);

        // Then: Await until 예약 상태가 COMPLETE가 되고, 결제까지 연결되었는지 확인
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            // 1. 예약 상태 확인
            Reservation reservation = reservationRepository.findByReservationId(saved.reservationId());
            assertThat(reservation.getReservationStatus()).isEqualTo(ReservationStatus.COMPLETE);

            // 2. 결제 내역이 등록되었는지 확인
            List<PaymentInfo> payments = paymentService.getUserPaymentList(userId);
            assertThat(payments).isNotEmpty();
            PaymentInfo latestPayment = payments.get(payments.size() - 1);
            assertThat(reservation.getPaymentId()).isEqualTo(latestPayment.paymentId());

            // 3. 좌석 상태가 비활성화 되었는지 확인
            Seat updatedSeat = seatRepository.findById(savedSeat.getId()).orElseThrow();
            assertThat(updatedSeat.getSeatStatus()).isEqualTo(SeatStatus.DISABLE);
        });

//        List<PaymentInfo> paymentList = paymentService.getUserPaymentList(userId);
//        Optional<Seat> seat = seatRepository.findById(savedSeat.getId());
//        Thread.sleep(10000);
//        Reservation info = reservationRepository.findByReservationId(saved.reservationId());
//        assertThat(info.getUserId()).isEqualTo(userId);
//        assertThat(info.getReservationStatus()).isEqualTo(ReservationStatus.COMPLETE);
//        PaymentInfo latestPayment = paymentList.get(paymentList.size()-1);
//        assertThat(info.getPaymentId()).isEqualTo(latestPayment.paymentId());
//        assertThat(seat.get().getSeatStatus()).isEqualTo(SeatStatus.DISABLE);
    }

}