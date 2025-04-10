package kr.hhplus.be.server.domain.concert.concertVO;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Value
public class ConcertName {
    String concertName;

    public ConcertName(String concertName){
        if(concertName == null || concertName.trim().isEmpty()){
            throw new IllegalArgumentException("콘서트 이름은 필수입니다.");
        }
        this.concertName = concertName;
    }

}
