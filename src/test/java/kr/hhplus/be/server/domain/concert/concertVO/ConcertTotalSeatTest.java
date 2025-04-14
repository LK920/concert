package kr.hhplus.be.server.domain.concert.concertVO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ConcertTotalSeatTest {

    @Test
    @DisplayName("콘서트 좌석 수가 0 이하이면 예외 발생시킨다.")
    void invalidTotalSeat(){
        assertThatThrownBy(()-> new ConcertTotalSeat(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("콘서트 좌석 수는 1이상이어야 합니다.");
    }

    @Test
    @DisplayName("콘서트 좌석수 생성")
    void concertTotalSeat(){
        ConcertTotalSeat totalSeat = new ConcertTotalSeat(50);
        assertThat(totalSeat.getConcertTotalSeat()).isEqualTo(50);
    }

    @Test
    @DisplayName("콘서트 좌석 수 증가할 수 있다.")
    void increase() {
        ConcertTotalSeat totalSeat = new ConcertTotalSeat(20);
        ConcertTotalSeat increased = totalSeat.increase(10);

        assertThat(increased.getConcertTotalSeat()).isEqualTo(30);

    }

    @Test
    @DisplayName("콘서트 좌석 수 감소할 수 있다.")
    void decrease() {
        ConcertTotalSeat totalSeat = new ConcertTotalSeat(30);
        ConcertTotalSeat decreased = totalSeat.decrease(10);

        assertThat(decreased.getConcertTotalSeat()).isEqualTo(20);
    }

    @Test
    @DisplayName("감소할 좌석 수가 기존 좌석수이상이면 예외처리한다")
    void decreaseSeatBeyondLimit(){
        ConcertTotalSeat totalSeat = new ConcertTotalSeat(30);

        assertThatThrownBy(()->totalSeat.decrease(30))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 좌석 수 이상을 감소할 수 없습니다.");
    }
}