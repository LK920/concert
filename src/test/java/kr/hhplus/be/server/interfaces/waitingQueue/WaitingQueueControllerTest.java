package kr.hhplus.be.server.interfaces.waitingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.RedisTestConfig;
import kr.hhplus.be.server.interfaces.waitingQueue.request.RequestToken;
import kr.hhplus.be.server.interfaces.waitingQueue.response.ResponseQueue;
import kr.hhplus.be.server.interfaces.waitingQueue.response.ResponseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
class WaitingQueueControllerIntegrationTest extends RedisTestConfig {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    void setup() {
        redissonClient.getKeys().flushall(); // Redis 초기화
    }

    @Test
    @DisplayName("대기열_등록_성공")
    void createWaitingQueue_success() throws Exception {
        long userId = 1L;

        String response = mvc.perform(get("/queue/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResponseToken result = objectMapper.readValue(response, ResponseToken.class);
        assertThat(result.token()).isNotBlank();
    }

    @Test
    @DisplayName("대기열_상태조회_ACTIVE_or_WAITING")
    void waitingQueue_success() throws Exception {
        // 대기열 등록
        long userId = 42L;

        String response = mvc.perform(get("/queue/{userId}", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResponseToken token = objectMapper.readValue(response, ResponseToken.class);
        RequestToken request = new RequestToken(userId, token.token());

        // 상태 조회
        String waitingResponse = mvc.perform(post("/queue/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Queue-token", token.token())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token.token()))
                .andExpect(jsonPath("$.tokenStatus").value(org.hamcrest.Matchers.anyOf(
                        org.hamcrest.Matchers.is("ACTIVE"),
                        org.hamcrest.Matchers.is("WAITING")
                )))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ResponseQueue result = objectMapper.readValue(waitingResponse, ResponseQueue.class);
        assertThat(result.token()).isEqualTo(token.token());
    }
}