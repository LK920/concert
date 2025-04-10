package kr.hhplus.be.server.application.reservation;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationFacade {

    private final PointService pointService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final SeatService seatService;

    public ReservationInfo reserveConcert(ReserveConcertCommand reserveConcertCommand){
        // 좌석 상태 변경(사용 가능 -> 불가능)
        seatService.reserveSeat(reserveConcertCommand.seatId());
        // 예약 내역 등록
        ReservationInfo reservationInfo = reservationService.createReservation(reserveConcertCommand.userId(), reserveConcertCommand.seatId());
        try{
            return processReservationTransaction(reserveConcertCommand, reservationInfo);
        }catch (Exception e){
            log.warn("예약 처리 중 에러 발생, 예약 대기 내역 ID : {} ", reservationInfo.reservationId());
            return reservationInfo;
        }
    }

    @Transactional
    public ReservationInfo processReservationTransaction(ReserveConcertCommand reserveConcertCommand, ReservationInfo reservationInfo){
        // 유저 포인트 차감
        pointService.useUserPoint(reserveConcertCommand.userId(),reserveConcertCommand.seatPrice());
                // 결제 추가
        PaymentInfo paymentInfo = paymentService.createPayment(reservationInfo.userId(), reserveConcertCommand.seatPrice(), PaymentType.USE);
        // 예약 내역에 결제 아이디 추가
        return reservationService.updatePaymentInfo(reservationInfo.reservationId(), paymentInfo.paymentId());
    }

}
