package kr.hhplus.be.server.interfaces.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.point.PointCommand;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.interfaces.point.request.PointRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.mockito.Mockito.*;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PointFacade pointFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this); // 각 테스트 실행전 모킹 초기화
    }

    @Test
    @DisplayName("사용자 포인트 조회 성공")
    void getUserPoint_success() throws Exception {
        // given
        long userId = 1L;
        int userPoint = 1000;
        when(pointFacade.getUserPoint(userId))
                .thenReturn(new PointCommand(userId, userPoint));

        // when & then
        mvc.perform(get("/point/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userPoint").value(userPoint));

        verify(pointFacade, times(1)).getUserPoint(userId);
    }

    @Test
    @DisplayName("사용자 포인트 충전 성공")
    void chargeUserPoint_success() throws Exception {
        // given
        long userId = 1L;
        int amount = 500;
        int chargedPoint = 1500;

        PointRequestDTO requestDTO = new PointRequestDTO(userId, amount);

        when(pointFacade.chargePoint(userId, amount))
                .thenReturn(new PointCommand(userId, chargedPoint));

        // when & then
        mvc.perform(post("/point/charge")
                        .contentType(MediaType.APPLICATION_JSON) // 컨텐츠 타입 명시
                        .content(objectMapper.writeValueAsString(requestDTO))) // 객체를 json 문자열로 변환해서 body에 포함
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userPoint").value(chargedPoint));

        verify(pointFacade, times(1)).chargePoint(userId, amount);
    }
}