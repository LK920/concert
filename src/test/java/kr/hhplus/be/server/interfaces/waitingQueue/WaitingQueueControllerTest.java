package kr.hhplus.be.server.interfaces.waitingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.queue.*;
import kr.hhplus.be.server.interfaces.waitingQueue.request.RequestToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = WaitingQueueController.class)
class WaitingQueueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WaitingQueueService waitingQueueService;

    @Autowired
    private ObjectMapper objectMapper;

    private String token = UUID.randomUUID().toString();
    private String invalidToken = "invalidToken";

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this); // 각 테스트 실행전 모킹 초기화
    }

    @Test
    @DisplayName("대기열_등록")
    void createWaitingQueue_success() throws Exception {
        long userId = 1l;
        WaitingQueue waitingQueue = WaitingQueue.create(token, userId);
        WaitingQueueInfo info = WaitingQueueInfo.from(waitingQueue);

        when(waitingQueueService.createWaitingQueue(userId)).thenReturn(info);

        mvc.perform(get("/queue/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    @DisplayName("대기열_등록_실패_토큰_있음")
    void createWaitingQueue_fail_token() throws Exception {
        long userId =1l;

        mvc.perform(get("/queue/{userId}", userId)
                .header("X-Queue-token", token))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("토큰이 없어야 합니다."));
    }

    @Test
    @DisplayName("대기번호_조회_성공")
    void waitingQueue() throws Exception {
        long userId = 1l;
        long remainedMillis = 1000l;
        long waitingNumber = 1l;
        RequestToken requestToken = new RequestToken(userId, token);
        WaitingQueue waitingQueue = WaitingQueue.create(token, userId);
        WaitingQueueDetail waitingQueueDetail = WaitingQueueDetail.from(waitingQueue, waitingNumber, remainedMillis);

        when(waitingQueueService.getWaitingQueue(token)).thenReturn(waitingQueueDetail);

        mvc.perform(post("/queue/waiting")
                    .header("X-Queue-token", token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.remainedMillis").value(remainedMillis))
                .andExpect(jsonPath("$.waitingNumber").value(waitingNumber))
                .andExpect(jsonPath("$.tokenStatus").value("WAITING"));

    }

    @Test
    @DisplayName("대기열_조회_헤더_토큰이_없으면_실패")
    void waitingQueue_fail_no_header_token() throws Exception {
        long userId = 1l;
        RequestToken requestToken = new RequestToken(userId, token);

        mvc.perform(post("/queue/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("토큰이 필요합니다."));
    }

    @Test
    @DisplayName("대기열_조회_잘못된_토큰_형식_실패")
    void waitingQueue_fail_invalidToken() throws Exception {
        long userId = 1l;
        RequestToken requestToken = new RequestToken(userId, token);

        mvc.perform(post("/queue/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestToken))
                        .header("X-Queue-token", invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("유효하지 않는 토큰입니다."));
    }
}