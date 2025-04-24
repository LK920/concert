package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
    @Index(name = "idx_reservation_concert_seat_id", columnList = "concert_seat_id"),
    @Index(name = "idx_reservation_user_id", columnList = "user_id"),
    @Index(name = "idx_reservation_payment_id", columnList = "payment_id"),
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_reservation_concert_seat_id", columnNames = "concert_seat_id")
    }
)
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long concertSeatId;
    private long userId;
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;
    private Long paymentId;

    // private 생성자 => 외부 상태변경 주입 못하게 막기
    private Reservation(long concertSeatId, long userId){
        this.concertSeatId = concertSeatId;
        this.userId = userId;
        this.reservationStatus = ReservationStatus.PENDING;
    }
    // factory 패턴
    public static Reservation create(long concertSeatId, long userId){
        return new Reservation(concertSeatId, userId);
    }

    public void cancelReservation(){
        if(this.reservationStatus == ReservationStatus.CANCELED){
            throw new IllegalArgumentException("이미 취소된 예약입니다.");
        }
        this.reservationStatus = ReservationStatus.CANCELED;
    }

    public void completeReservation(){
        if(this.reservationStatus != ReservationStatus.PENDING){
            throw new IllegalArgumentException("대기중인 예약만 완료할 수 있습니다.");
        }

        if (this.paymentId == null) {
            throw new IllegalArgumentException("결제 내역이 추가되지 않았습니다.");
        }

        this.reservationStatus = ReservationStatus.COMPLETE;
    }

    public void addPaymentId(Long paymentId) {
        if (this.paymentId != null) {
            throw new IllegalArgumentException("이미 결제가 완료된 예약입니다.");
        }

        this.paymentId = paymentId;
    }

}
