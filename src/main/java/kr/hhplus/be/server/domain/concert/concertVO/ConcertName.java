package kr.hhplus.be.server.domain.concert.concertVO;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Value
public class ConcertName {
    String value;

    public ConcertName(String value){
        if(value == null || value.trim().isEmpty()){
            throw new IllegalArgumentException("콘서트 이름은 필수입니다.");
        }
        this.value = value;
    }

}
