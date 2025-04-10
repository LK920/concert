package kr.hhplus.be.server.domain.concertDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcertDateTest {

    @Test
    @DisplayName("공연 날짜 정상 생성")
    void createConcertDate(){

        long concertId = 1l;
        LocalDateTime concertDate = LocalDateTime.of(2025,05,05,22,00,00);

        ConcertDate created = ConcertDate.create(1l, concertDate);

        assertThat(created).isNotNull();
        assertThat(created.getConcertId()).isEqualTo(concertId);
        assertThat(created.getConcertDate()).isEqualTo(concertDate);
        assertThat(created.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("공연 날짜 Null이면 예외")
    void concertDateIsNull(){
        long concertId = 1l;
        assertThatThrownBy(()->ConcertDate.create(concertId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("공연 날짜를 입력해주세요");

    }

    @Test
    @DisplayName("공연 날짜 닫기")
    void closeConcertDate(){
        ConcertDate concertDate = ConcertDate.create(1l, LocalDateTime.now());
        concertDate.closeDateReservation();
        assertThat(concertDate.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("공연 날짜 열기")
    void openConcertDate(){
        ConcertDate concertDate = ConcertDate.create(1l, LocalDateTime.now());
        concertDate.closeDateReservation();

        concertDate.openDateReservation();

        assertThat(concertDate.isAvailable()).isTrue();
    }
}