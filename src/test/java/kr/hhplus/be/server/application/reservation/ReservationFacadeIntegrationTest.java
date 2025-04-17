package kr.hhplus.be.server.application.reservation;

import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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

        ReserveConcertCommand command = new ReserveConcertCommand(userId, savedSeat.getId(), savedSeat.getConcertSeatPrice());

        ReservationInfo info = reservationFacade.reserveConcert(command);

        List<PaymentInfo> paymentList = paymentService.getUserPaymentList(userId);

        assertThat(paymentList).isEmpty();
        assertThat(info.reservationStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(info.paymentId()).isNull();
    }

    @Test
    @DisplayName("콘서트_예약")
    void reserveConcert() {
        long userId = 1l;
        long chargePoint = 3000l;
        pointService.chargeUserPoint(userId,chargePoint);

        ReserveConcertCommand command = new ReserveConcertCommand(userId, savedSeat.getId(), savedSeat.getConcertSeatPrice());

        ReservationInfo info = reservationFacade.reserveConcert(command);

        List<PaymentInfo> paymentList = paymentService.getUserPaymentList(userId);
        Optional<Seat> seat = seatRepository.findById(savedSeat.getId());
        assertThat(info.userId()).isEqualTo(userId);
        assertThat(info.reservationStatus()).isEqualTo(ReservationStatus.COMPLETE);
        PaymentInfo latestPayment = paymentList.get(paymentList.size()-1);
        assertThat(info.paymentId()).isEqualTo(latestPayment.paymentId());
        assertThat(seat.get().getSeatStatus()).isEqualTo(SeatStatus.DISABLE);
    }

    @Test
    @DisplayName("예약_처리_성공")
    void processReservationTransaction_success() {
        long userId = 1L;
        long seatPrice = 3000L;
        PointInfo initialPoint = pointService.getUserPoint(userId);
        pointService.chargeUserPoint(userId, seatPrice);
        ReserveConcertCommand command = new ReserveConcertCommand(userId, savedSeat.getId(), savedSeat.getConcertSeatPrice());
        ReservationInfo reservationInfo = reservationService.createReservation(savedSeat.getId(), userId);
        List<PaymentInfo> paymentsBefore = paymentService.getUserPaymentList(userId);
        long initialPaymentSize = paymentsBefore.size();

        // when
        ReservationInfo updatedReservation = reservationFacade.processReservationTransaction(command, reservationInfo);

        // then
        List<PaymentInfo> paymentsAfter = paymentService.getUserPaymentList(userId);
        assertThat(paymentsAfter.size()).isEqualTo(initialPaymentSize + 1);

        PaymentInfo newPayment = paymentsAfter.get(paymentsAfter.size() - 1);

        assertThat(updatedReservation.paymentId()).isEqualTo(newPayment.paymentId());
        assertThat(updatedReservation.reservationStatus()).isEqualTo(ReservationStatus.COMPLETE);

        // 포인트가 0이 되었는지 확인
        PointInfo remainingPoint = pointService.getUserPoint(userId);
        assertThat(remainingPoint.userPoint()).isEqualTo(initialPoint.userPoint());
    }
}