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
        WaitingQueue queue = WaitingQueue.create(token,userId,datetime);
        // then
        assertThat(queue.getUserId()).isEqualTo(userId);
        assertThat(queue.getToken()).isEqualTo(token);
        assertThat(queue.getStatus()).isEqualTo(WaitingQueueStatus.WAITING);
        assertThat(queue.getExpiredAt()).isEqualTo(datetime.plusMinutes(expiredAt));
    }

    @Test
    void activate() {
        // given
        String token = "token-uuid";
        long userId = 1l;
        LocalDateTime datetime = LocalDateTime.now();
        WaitingQueue queue = WaitingQueue.create(token,userId,datetime);
        queue.activate();

        //when & then
        assertThatThrownBy(()->queue.activate()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대기 상태가 아닙니다.");
    }

    @Test
    void expire() {
        String token = "token-uuid";
        long userId = 1l;
        LocalDateTime datetime = LocalDateTime.now();
        WaitingQueue queue = WaitingQueue.create(token,userId,datetime);
        queue.expire();

        //when & then
        assertThatThrownBy(()->queue.expire()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 만료된 상태입니다.");
    }
}