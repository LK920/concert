package kr.hhplus.be.server.interfaces.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertInfo;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.concertDate.ConcertDateInfo;
import kr.hhplus.be.server.domain.concertDate.ConcertDateService;
import kr.hhplus.be.server.domain.seat.SeatInfo;
import kr.hhplus.be.server.domain.seat.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = ConcertController.class)
class ConcertControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    private ConcertService concertService;

    @MockitoBean
    private ConcertDateService concertDateService;

    @MockitoBean
    private SeatService concertSeatService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this); // 각 테스트 실행전 모킹 초기화
    }

    @DisplayName("콘서트 조회")
    @Test
    void getConcerts() throws Exception {
        // given
        ConcertInfo concertInfo1 = new ConcertInfo(1l, "concert1",50L);
        ConcertInfo concertInfo2 = new ConcertInfo(2l, "concert2",50L);
        List<ConcertInfo> concertList = List.of(concertInfo1, concertInfo2);
        when(concertService.getConcerts()).thenReturn(concertList);

        // when
        mvc.perform(get("/concert/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concertId").value(1L))
                .andExpect(jsonPath("$[0].concertName").value("concert1"))
                .andExpect(jsonPath("$[1].concertId").value(2L))
                .andExpect(jsonPath("$[1].concertName").value("concert2"));
        // then
        verify(concertService, times(1)).getConcerts();
    }

    @DisplayName("콘서트 날짜 조회")
    @Test
    void getConcertAvailableDates() throws Exception {
        // given
        long concertId = 1L;
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusHours(4l);
        ConcertDateInfo concertDateInfo01 = new ConcertDateInfo(1l, time1);
        ConcertDateInfo concertDateInfo02 = new ConcertDateInfo(2l, time2);
        when(concertDateService.getConcertAvailableDates(concertId)).thenReturn(List.of(concertDateInfo01, concertDateInfo02));
        // when
        mvc.perform(get("/concert/{concertId}/date", concertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concertDateId").value(concertDateInfo01.concertDateId()))
                .andExpect(jsonPath("$[0].concertDate").value(concertDateInfo01.concertDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$[1].concertDateId").value(concertDateInfo02.concertDateId()))
                .andExpect(jsonPath("$[1].concertDate").value(concertDateInfo02.concertDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        //then
        verify(concertDateService, times(1)).getConcertAvailableDates(concertId);
    }


    @Test
    @DisplayName("콘서트 좌석 조회 성공")
    void getConcertAvailableSeats() throws Exception {
        // given
        long concsertDateId = 1l;
        SeatInfo seatInfo01 = new SeatInfo(1L, 1L, 123l);
        SeatInfo seatInfo02 = new SeatInfo(2L, 2L, 123l);
        when(concertSeatService.getAvailableSeats(concsertDateId)).thenReturn(List.of(seatInfo01, seatInfo02));

        // when & then
        mvc.perform(get("/concert/{dateId}/seat", concsertDateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concertSeatId").value(seatInfo01.seatId()))
                .andExpect(jsonPath("$[0].concertSeatPrice").value(seatInfo01.seatPrice()))
                .andExpect(jsonPath("$[0].concertSeatNumber").value(seatInfo01.seatNumber()))
                .andExpect(jsonPath("$[1].concertSeatId").value(seatInfo02.seatId()))
                .andExpect(jsonPath("$[1].concertSeatPrice").value(seatInfo02.seatPrice()))
                .andExpect(jsonPath("$[1].concertSeatNumber").value(seatInfo02.seatNumber()));

        verify(concertSeatService, times(1)).getAvailableSeats(concsertDateId);
    }



}