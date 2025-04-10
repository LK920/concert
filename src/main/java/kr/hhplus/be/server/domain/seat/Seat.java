package kr.hhplus.be.server.domain.seat;

import kr.hhplus.be.server.domain.concert.SeatStatus;
import lombok.Getter;

@Getter
public class ConcertSeat {
    private final Long concertSeatId;
    private final Long concertDateId;
    private final Long concertSeatNumber;
    private SeatStatus seatStatus;
    private long concertSeatPrice;

    public ConcertSeat(Long concertSeatId, Long concertDateId, Long concertSeatNumber, long concertSeatPrice){
        if(concertSeatPrice <= 0){
            throw new IllegalArgumentException("가격은 1 이상이어야 합니다.");
        }

        if(seatStatus == null){
            throw new IllegalArgumentException("좌석 상태는 ");
        }

        this.concertSeatId = concertSeatId;
        this.concertDateId = concertDateId;
        this.concertSeatNumber = concertSeatNumber;
        this.seatStatus = SeatStatus.ENABLE;
        this.concertSeatPrice = concertSeatPrice;

    }

    public void reserveSeat() {
        if (this.seatStatus != SeatStatus.ENABLE) {
            throw new IllegalStateException("예약할 수 없는 좌석입니다.");
        }
        this.seatStatus = SeatStatus.DISABLE;
    }

    public void cancelSeatReservation(){
        if (this.seatStatus != SeatStatus.DISABLE) {
            throw new IllegalStateException("이미 취소된 좌석입니다.");
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
