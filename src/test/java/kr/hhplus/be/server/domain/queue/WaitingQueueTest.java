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
    void isActived() {
        // given
        String token = "token-uuid";
        long userId = 1l;
        LocalDateTime datetime = LocalDateTime.now();
        WaitingQueue queue = WaitingQueue.create(token,userId,datetime);
        queue.isActived();

        //when & then
        assertThatThrownBy(()->queue.isActived()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("대기 상태가 아닙니다.");
    }

    @Test
    void isExpired() {
        String token = "token-uuid";
        long userId = 1l;
        LocalDateTime datetime = LocalDateTime.now();
        WaitingQueue queue = WaitingQueue.create(token,userId,datetime);
        queue.isExpired();

        //when & then
        assertThatThrownBy(()->queue.isExpired()).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 만료된 상태입니다.");
    }
}