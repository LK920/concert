package kr.hhplus.be.server.domain.concert.concertVO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ConcertNameTest {

    @Test
    @DisplayName("콘서트 이름이 null 이면 예외를 발생시킨다.")
    void nullConcertNameThrowsException() {
        assertThatThrownBy(() -> new ConcertName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("콘서트 이름은 필수입니다.");
    }

    @Test
    @DisplayName("콘서트 이름이 빈값이면 예외를 발생시킨다.")
    void emptyConcertNameThrowsException(){
        assertThatThrownBy(()-> new ConcertName(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("콘서트 이름은 필수입니다.");
    }

    @Test
    @DisplayName("콘서트 이름 정상 생성")
    void concertName(){
        ConcertName concertName = new ConcertName("정상 생성 콘서트");
        assertThat(concertName.getConcertName()).isEqualTo("정상 생성 콘서트");
    }

}