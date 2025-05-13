package kr.hhplus.be.server.application.reservation;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.support.redis.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationFacade {

    private final PointService pointService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final SeatService seatService;
    private final RedisLock redisLock;

    public ReservationInfo reserveConcert(ReserveConcertCommand reserveConcertCommand){
        String lockKey = "lock:seat:" + reserveConcertCommand.seatId();
        String lockValue = "lock:value:" + UUID.randomUUID().toString();
        boolean getLock = redisLock.tryLock(lockKey, lockValue);

        if(!getLock){
            throw new IllegalArgumentException("락 획득 실패했습니다.");
        }
        log.info("락 획득을 성공했습니다.");
        try {
            seatService.reserveSeat(reserveConcertCommand.seatId());
            ReservationInfo reservationInfo = reservationService.createReservation(reserveConcertCommand.userId(), reserveConcertCommand.seatId());
            log.info("예약은 성공");
            return processReservationTransaction(reserveConcertCommand, reservationInfo);

        }catch (IllegalArgumentException illegalArgumentException){
            // 좌석 중복 예외
            log.error(illegalArgumentException.getMessage());
            throw new IllegalArgumentException(illegalArgumentException.getMessage(), illegalArgumentException);
            
        }catch (Exception e){
            log.error("예외 메시지: {}", e.getMessage(), e);
            throw new RuntimeException("예외 발생", e);
        } finally {
            redisLock.unLock(lockKey, lockValue);
            log.info("락을 해제합니다.");
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
