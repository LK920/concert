package kr.hhplus.be.server.domain.queue;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class WaitingQueueTest {

    @Test
    void create() {
        // given
        String token = "token-uuid";
        long userId = 1l;
        LocalDateTime datetime = LocalDateTime.of(2025,05,05,20,00,00);
        long expiredAt = 10;
        // when
        WaitingQueue queue = WaitingQueue.create(token,userId);
        // then
        assertThat(queue.getUserId()).isEqualTo(userId);
        assertThat(queue.getToken()).isEqualTo(token);
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.WAITING);
        assertThat(queue.getExpiredAt()).isNull();
    }

    @Test
    void isActivated() {
        // given
        String token = "token-uuid";
        long userId = 1l;
        long activeQueueCount = 3;
        long maxCapacity = 3;
        WaitingQueue queue = WaitingQueue.create(token,userId);
        queue.active();

        //when & then
        boolean queueStatus = queue.isActivated(activeQueueCount, maxCapacity);

        assertThat(queueStatus).isFalse();
    }

    @Test
    void isExpired() {
        String token = "token-uuid";
        long userId = 1l;
        LocalDateTime datetime = LocalDateTime.now();
        WaitingQueue queue = WaitingQueue.create(token,userId);
        queue.expire();

        //when & then
        boolean queueStatus = queue.isExpired(datetime);

        assertThat(queueStatus).isFalse();
    }
}