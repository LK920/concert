package kr.hhplus.be.server.domain.concert.concertVO;

import lombok.Data;
import lombok.Value;

@Value
public class ConcertTotalSeat {
    long value;

    public ConcertTotalSeat(long value){
        if(value <= 0){
            throw new IllegalArgumentException("콘서트 좌석 수는 1이상이어야 합니다.");
        }
        this.value = value;
    }

    public ConcertTotalSeat increase(long count){
        if(count <= 0){
            throw  new IllegalArgumentException("증가할 좌석 수는 1 이상이어야 합니다.");
        }

        return new ConcertTotalSeat(this.value + count);
    }

    public ConcertTotalSeat decrease(long count){
        if(count <= 0){
           throw  new IllegalArgumentException("감소할 좌석 수는 1 이상이어야 합니다.");
        }
        long newValue = this.value - count;
        if(newValue <= 0){
           throw  new IllegalArgumentException("기존 좌석 수 이상을 감소할 수 없습니다.");
        }

        return new ConcertTotalSeat(newValue);
    }

}
