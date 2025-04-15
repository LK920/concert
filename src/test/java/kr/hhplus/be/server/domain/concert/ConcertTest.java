package kr.hhplus.be.server.domain.concert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcertTest {

    @Test
    @DisplayName("콘서트 생성 성공")
    void create() {
        // given
        String concertName = "Spring Festival";
        long totalSeats = 100;

        // when
        Concert concert = Concert.create(concertName, totalSeats);

        // then
        assertThat(concert.getConcertName().getConcertName()).isEqualTo(concertName);
        assertThat(concert.getConcertTotalSeat().getConcertTotalSeat()).isEqualTo(totalSeats);
    }

    @Test
    @DisplayName("콘서트 이름 변경")
    void changeConcertName() {
        Concert concert = Concert.create("oldName", 1);
        concert.changeConcertName("newName");
        assertThat(concert.getConcertName().getConcertName()).isEqualTo("newName");
    }

    @Test
    @DisplayName("좌석 수를 증가 성공")
    void increaseSeats() {
        Concert concert = Concert.create("concertName", 50);
        concert.increaseSeats(40);
        assertThat(concert.getConcertTotalSeat().getConcertTotalSeat()).isEqualTo(90);
    }

    @Test
    @DisplayName("좌석 수 감소 성공")
    void decreaseSeats() {
        Concert concert = Concert.create("concertName", 50);
        concert.decreaseSeats(40);
        assertThat(concert.getConcertTotalSeat().getConcertTotalSeat()).isEqualTo(10);
    }


}