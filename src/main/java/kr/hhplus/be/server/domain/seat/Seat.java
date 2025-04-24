package kr.hhplus.be.server.domain.seat;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = @Index(name = "idx_seat_concert_date_id", columnList = "concert_date_id")
)
public class Seat extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long concertDateId;
    private long concertSeatNumber;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;
    private long concertSeatPrice;
    @Version
    private long version;

    private Seat(long concertDateId, long concertSeatNumber, long concertSeatPrice){
        if(concertSeatPrice <= 0){
            throw new IllegalArgumentException("가격은 1 이상이어야 합니다.");
        }

        this.concertDateId = concertDateId;
        this.concertSeatNumber = concertSeatNumber;
        this.seatStatus = SeatStatus.ENABLE;
        this.concertSeatPrice = concertSeatPrice;
    }

    public static Seat create(long concertDateId, long concertSeatNumber, long concertSeatPrice){
        return new Seat(concertDateId, concertSeatNumber, concertSeatPrice);
    }

    public void reserveSeat() {
        if (this.seatStatus != SeatStatus.ENABLE) {
            throw new IllegalArgumentException("예약할 수 없는 좌석입니다.");
        }
        this.seatStatus = SeatStatus.DISABLE;
    }

    public void cancelSeatReservation(){
        if (this.seatStatus != SeatStatus.DISABLE) {
            throw new IllegalArgumentException("이미 취소된 좌석입니다.");
        }
        this.seatStatus = SeatStatus.ENABLE;
    }

    public void changeSeatPrice(long price){
        if(price <= 0){
            throw new IllegalArgumentException("가격은 1 이상 정수만 가능합니다.");
        }

        this.concertSeatPrice = price;
    }
}
