package kr.hhplus.be.server.domain.concert.concertVO;

import jakarta.persistence.Embeddable;
import lombok.*;
/*
* Embaddable => entity의 vo를 칼럼처럼 사용하게 해준다.
* jpa 엔터티로 만들고
* */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConcertName {
    String concertName;

    public ConcertName(String concertName){
        if(concertName == null || concertName.trim().isEmpty()){
            throw new IllegalArgumentException("콘서트 이름은 필수입니다.");
        }
        this.concertName = concertName;
    }

}
