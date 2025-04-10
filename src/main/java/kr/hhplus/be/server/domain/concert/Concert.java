package kr.hhplus.be.server.domain.concert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
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
    private String concertName;
    private long concertTotalSeats;

    private Concert(String concertName, long concertTotalSeats){
        if(concertName == null){
            throw new IllegalArgumentException("콘서트 이름은 필수입니다.");
        }
        this.concertName = concertName;
        this.concertTotalSeats = concertTotalSeats;
    }

    public static Concert create(String concertName, long concertTotalSeats){
        return new Concert(concertName, concertTotalSeats);
    }

    public void changeConcertName(String concertName){
        if(concertName == null){
            throw new IllegalArgumentException("콘서트 이름은 필수입니다.");
        }

        this.concertName = concertName;
    }

    public void increaseSeats(long count){
        if(count <= 0){
            throw new IllegalArgumentException("추가할 좌석 수는 1 이상이어야 합니다.");
        }

        this.concertTotalSeats = this.getConcertTotalSeats() + count;
    }

    public void decreaseSeats(long count){
        if(count <= 0){
            throw new IllegalArgumentException("뺄 좌석 수는 1 이상이어야 합니다.");
        }

        if(this.concertTotalSeats - count < 0){
            throw new IllegalArgumentException("좌석 수는 0 이상이어야 합니다.");
        }

        this.concertTotalSeats = this.getConcertTotalSeats() - count;
    }

}
