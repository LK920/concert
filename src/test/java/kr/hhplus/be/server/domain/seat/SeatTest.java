package kr.hhplus.be.server.domain.seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class SeatTest {

    @Test
    @DisplayName("좌석 생성 - 좌석 가격 음수")
    void create_negativePrice(){
        long conertId = 1L;
        long concertSeatNumb = 1L;
        long concertSeatPrice = -1L;

        assertThatThrownBy(()->Seat.create(conertId,concertSeatNumb,concertSeatPrice)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 1 이상이어야 합니다.");

    }

    @Test
    @DisplayName("좌석 예약 - 이미 예약된 좌석 예외")
    void reserveSeat_alreadyReserve(){
        Seat seat = Seat.create(1L, 1L, 2000L);
        seat.reserveSeat();
        assertThatThrownBy(seat::reserveSeat).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약할 수 없는 좌석입니다.");
    }

    @Test
    @DisplayName("좌석 취소 - 이미 사용중인 좌석")
    void cancelSeatReservation_alreadyCanceled(){
        Seat seat = Seat.create(1L, 1L, 2000L);

        assertThatThrownBy(seat::cancelSeatReservation).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 취소된 좌석입니다.");
    }

    @Test
    @DisplayName("금액 변경 - 변경 금액 음수")
    void changeSeatPrice_InvalidPrice(){
        Seat seat = Seat.create(1L, 1L, 2000L);

        assertThatThrownBy(()->seat.changeSeatPrice(-1)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 1 이상 정수만 가능합니다.");
    }
}