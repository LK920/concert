package kr.hhplus.be.server.domain.reservation;

import kr.hhplus.be.server.domain.events.ReservationCompletedEvent;
import kr.hhplus.be.server.domain.events.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.events.ReservationFailedEvent;
import kr.hhplus.be.server.infra.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DomainEventPublisher domainEventPublisher;

    // 예약 생성
    @Transactional
    public ReservationInfo createReservation(long userId, long concertId, long seatId, long seatPrice){
        try{
            Reservation reservation = Reservation.create(seatId,userId);
            Reservation saved = reservationRepository.save(reservation);

            domainEventPublisher.publish(
                    new ReservationCreatedEvent(userId, concertId, saved.getId(), seatId, seatPrice));

            return ReservationInfo.from(saved);
        } catch (DataIntegrityViolationException e) {
            log.warn("이미 예약된 좌석입니다.");
            // 예약 실패 후 좌석 해제 이벤트 발행
            domainEventPublisher.publish(new ReservationFailedEvent(seatId));
            throw new DataIntegrityViolationException("이미 예약된 좌석입니다.");
        }
    }

    // 예약 취소
    @Transactional
    public ReservationInfo cancelReservation(long reservationId){
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        reservation.cancelReservation();
        Reservation saved = reservationRepository.save(reservation);
        return ReservationInfo.from(saved);
    }

    // 결제 id 추가
    @Transactional
    public ReservationInfo updatePaymentInfo(long reservationId, long paymentId){
        Reservation reservation = reservationRepository.findByReservationId(reservationId);
        reservation.addPaymentId(paymentId);
        reservation.completeReservation();
        Reservation saved = reservationRepository.save(reservation);
        return ReservationInfo.from(saved);
    }

    // 예약 내역 조회
    @Transactional(readOnly = true)
    public List<ReservationInfo> getUserReservationInfo(long userId){
        List<Reservation> reservationList = reservationRepository.getUserReservationList(userId);
        return reservationList.stream().map(
                reservation -> ReservationInfo.from(reservation)
        ).toList();
    }

}
