package kr.hhplus.be.server.domain.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // 예약 생성
    @Transactional
    public ReservationInfo createReservation(long seatId, long userId){
        // 예약 내역 저장만 한다
        Reservation reservation = Reservation.create(seatId,userId);
        Reservation saved = reservationRepository.save(reservation);

        return ReservationInfo.from(saved);
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
