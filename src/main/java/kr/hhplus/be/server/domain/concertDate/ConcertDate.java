package kr.hhplus.be.server.domain.concertDate;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_concert_date_concert_id", columnList = "concert_id"))
public class ConcertDate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long concertId;
    private LocalDateTime concertDate;
    private boolean isAvailable;

    private ConcertDate(long concertId, LocalDateTime concertDate){
        if(concertDate == null){
            throw new IllegalArgumentException("공연 날짜를 입력해주세요");
        }
        this.concertId = concertId;
        this.concertDate = concertDate;
        this.isAvailable = true;
    }

    public static ConcertDate create(long concertId, LocalDateTime concertDate){
        return new ConcertDate(concertId, concertDate);
    }

    public void closeDateReservation() {
        this.isAvailable = false;
    }

    public void openDateReservation(){
        this.isAvailable = true;
    }

}
