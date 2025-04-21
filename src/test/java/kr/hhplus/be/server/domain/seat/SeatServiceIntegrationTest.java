package kr.hhplus.be.server.domain.seat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class SeatServiceIntegrationTest {

    @Autowired
    private SeatService seatService;
    @Autowired
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp(){
        seatRepository.deleteAll();
    }

    @Test
    @DisplayName("이용가능한_좌석_조회")
    void getAvailableSeats() {
        long concertDateId = 1l;
        Seat s1 = Seat.create(concertDateId, 1,20000);
        Seat s2 = Seat.create(concertDateId, 2,20000);
        Seat s3 = Seat.create(concertDateId, 3,20000);
        Seat s4 = Seat.create(concertDateId, 4,20000);
        seatRepository.saveAll(List.of(s1,s2,s3,s4));

        List<SeatInfo> result = seatService.getAvailableSeats(concertDateId);

        assertThat(result).isNotNull().hasSize(4);
    }

    @Test
    @DisplayName("좌석_예약_실패")
    void reserveSeat_fail() {
        long concertDateId = 1l;
        Seat s1 = Seat.create(concertDateId, 1,20000);
        s1.reserveSeat();
        Seat saved = seatRepository.save(s1);

        assertThatThrownBy(()->seatService.reserveSeat(saved.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약할 수 없는 좌석입니다.");
    }

    @Test
    @DisplayName("좌석_예약_성공")
    void reserveSeat_success() {
        long concertDateId = 1l;
        Seat s1 = Seat.create(concertDateId, 1,20000);
        Seat saved = seatRepository.save(s1);

        seatService.reserveSeat(saved.getId());

        Optional<Seat> seat = seatRepository.findById(saved.getId());

        assertThat(seat).isNotNull();
        assertThat(seat.get().getConcertDateId()).isEqualTo(concertDateId);
        assertThat(seat.get().getConcertSeatPrice()).isEqualTo(20000);
        assertThat(seat.get().getConcertSeatNumber()).isEqualTo(1);
        assertThat(seat.get().getSeatStatus()).isEqualTo(SeatStatus.DISABLE);
    }
}