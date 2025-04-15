package kr.hhplus.be.server.domain.concert;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.domain.concert.concertVO.ConcertName;
import kr.hhplus.be.server.domain.concert.concertVO.ConcertTotalSeat;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private ConcertName concertName;
    @Embedded
    private ConcertTotalSeat concertTotalSeat;

    private Concert(ConcertName concertName, ConcertTotalSeat concertTotalSeat){
        this.concertName = concertName;
        this.concertTotalSeat = concertTotalSeat;
    }

    public static Concert create(String concertName, long concertTotalSeats) {
        return new Concert(new ConcertName(concertName), new ConcertTotalSeat(concertTotalSeats));
    }

    public void changeConcertName(String concertName) {
        this.concertName = new ConcertName(concertName);
    }

    public void increaseSeats(long count) {
        this.concertTotalSeat = this.concertTotalSeat.increase(count);
    }

    public void decreaseSeats(long count) {
        this.concertTotalSeat = this.concertTotalSeat.decrease(count);
    }

}
