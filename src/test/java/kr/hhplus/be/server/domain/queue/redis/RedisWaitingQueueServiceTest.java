package kr.hhplus.be.server.domain.queue.redis;

import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RedisWaitingQueueServiceTest {

    private WaitingQueueStorage waitingQueueStorage;
    private RedisWaitingQueueService waitingQueueService;

    @BeforeEach
    void setUp() {
        waitingQueueStorage = mock(WaitingQueueStorage.class);
        waitingQueueService = new RedisWaitingQueueService(waitingQueueStorage);
    }

    @Test
    @DisplayName("대기열 토큰 생성")
    void createWaitingQueue() {
        // given
        long userId = 123L;
        String token = UUID.randomUUID().toString();

        // when
        RedisQueueInfo result = waitingQueueService.createWaitingQueue(userId);


        // then ArgumentCaptor
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(waitingQueueStorage).enqueue(tokenCaptor.capture(), eq(userId));

        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(tokenCaptor.getValue());
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.status()).isEqualTo(WaitingQueueStatus.WAITING);
    }

    @Test
    @DisplayName("대기열 토큰 조회시 토큰이 활성토큰일 경우")
    void getWaitingQueue_active_token() {
        // given
        String token = UUID.randomUUID().toString();
        long userId = 123L;

        when(waitingQueueStorage.getUserId(token)).thenReturn(userId);
        when(waitingQueueStorage.isActive(token)).thenReturn(true);

        // when
        RedisQueueStatusResponse response = waitingQueueService.getWaitingQueue(token);

        // then
        assertThat(response.status()).isEqualTo(WaitingQueueStatus.ACTIVE);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.token()).isEqualTo(token);
    }

    @Test
    @DisplayName("대기열 조회 시 활성토큰으로 변경되는 경우 ")
    void getWaitingQueue_availableSlot_activeToken() {
        // given
        String token = UUID.randomUUID().toString();
        long userId = 123L;

        when(waitingQueueStorage.getUserId(token)).thenReturn(userId);
        when(waitingQueueStorage.isActive(token)).thenReturn(false);
        when(waitingQueueStorage.getActiveCount()).thenReturn(2); // MAX_ACTIVE_USERS = 3

        // when
        RedisQueueStatusResponse response = waitingQueueService.getWaitingQueue(token);

        // then
        verify(waitingQueueStorage).activate(token);
        assertThat(response.status()).isEqualTo(WaitingQueueStatus.ACTIVE);
    }

    @Test
    @DisplayName("대기열 조회 시 대기 토큰일 경우")
    void getWaitingQueue_notAvailableSlot_waitingToken() {
        // given
        String token = UUID.randomUUID().toString();
        long userId = 123L;

        when(waitingQueueStorage.getUserId(token)).thenReturn(userId);
        when(waitingQueueStorage.isActive(token)).thenReturn(false);
        when(waitingQueueStorage.getActiveCount()).thenReturn(3); // Full
        when(waitingQueueStorage.getWaitingNumber(token)).thenReturn(5);
        when(waitingQueueStorage.getOldestActiveRemainingMillis()).thenReturn(30000L);

        // when
        RedisQueueStatusResponse response = waitingQueueService.getWaitingQueue(token);

        // then
        assertThat(response.status()).isEqualTo(WaitingQueueStatus.WAITING);
        assertThat(response.waitingNumber()).isEqualTo(5);
        assertThat(response.remainedMillis()).isEqualTo(30000L);
    }

    @Test
    @DisplayName("토큰 갱신 활성 가능한 경우")
    void refreshWaitingQueueStatus() {
        // given
        when(waitingQueueStorage.getActiveCount()).thenReturn(1); // 2자리 남음

        // when
        waitingQueueService.refreshWaitingQueueStatus();

        // then
        verify(waitingQueueStorage).expireInactiveTokens();
        verify(waitingQueueStorage).activateFromWaiting(2);
    }

    @Test
    @DisplayName("토큰 대기 활성 불가일 경우")
    void refreshWaitingQueueStatus_notAvailableSlot() {
        // given
        when(waitingQueueStorage.getActiveCount()).thenReturn(3); // Full

        // when
        waitingQueueService.refreshWaitingQueueStatus();

        // then
        verify(waitingQueueStorage).expireInactiveTokens();
        verify(waitingQueueStorage, never()).activateFromWaiting(anyInt());
    }
}
