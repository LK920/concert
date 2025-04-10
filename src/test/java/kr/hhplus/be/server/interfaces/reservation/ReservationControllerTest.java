package kr.hhplus.be.server.interfaces.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReserveConcertCommand;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationStatus;
import kr.hhplus.be.server.interfaces.reservation.request.ReserveRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationFacade reservationFacade;

    @Test
    @DisplayName("콘서트 예약 성공")
    void reserveConcert_Success() throws Exception {
        // given
        long userId = 1L;
        long seatId = 10L;
        long seatPrice = 50000L;
        long paymentId = 1l;

        ReserveRequestDTO requestDTO = new ReserveRequestDTO(userId, seatId, seatPrice);
        ReserveConcertCommand reserveConcertCommand = new ReserveConcertCommand(userId,seatId,seatPrice);
        ReservationInfo reservationInfo = new ReservationInfo(
                100L, // reservationId
                userId,
                seatId,
                ReservationStatus.COMPLETE, // enum 이라고 가정
                paymentId
        );

        when(reservationFacade.reserveConcert(reserveConcertCommand))
                .thenReturn(reservationInfo);

        // when & then
        mvc.perform(post("/reservation/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(100))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.seatId").value(seatId))
                .andExpect(jsonPath("$.reservationStatus").value("COMPLETE"));

        verify(reservationFacade, times(1)).reserveConcert(reserveConcertCommand);

    }
}