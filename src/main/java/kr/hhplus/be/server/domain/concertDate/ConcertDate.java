package kr.hhplus.be.server.domain.concert;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public boolean isPastDate(){
        return this.concertDate.isBefore(LocalDateTime.now());
    }
}
