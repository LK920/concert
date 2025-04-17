package kr.hhplus.be.server.domain.queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(TestcontainersConfiguration.class)
class WaitingQueueServiceIntegrationTest {
    @Autowired
    private WaitingQueueService waitingQueueService;
    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @BeforeEach
    void setUp(){
        waitingQueueRepository.deleteAll();
    }

    @Test
    @DisplayName("대기열_생성_성공")
    void createWaitingQueue_success() {
        long userId = 1l;

        WaitingQueueInfo result = waitingQueueService.createWaitingQueue(userId);

        assertThat(result.expiredAt()).isNull();
        assertThat(result.status()).isEqualTo(WaitingQueueStatus.WAITING);
        assertThat(result.userId()).isEqualTo(userId);

    }

    @Test
    @DisplayName("대기열_조회_토큰_없음")
    void getWaitingQueue_fail_noToken() {
        String unknownToken = "noToken";

        assertThatThrownBy(()-> waitingQueueService.getWaitingQueue(unknownToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 토큰입니다.");
    }

    @Test
    @DisplayName("대기열_조회_성공_대기상태")
    void getWaitingQueue_success_waiting() {
        for (long i = 0 ; i < 3; i++){
            WaitingQueueInfo info = waitingQueueService.createWaitingQueue(i); // 대기열 생성
            waitingQueueService.getWaitingQueue(info.token());  // 생성된 대기열 active 전환
        }

        WaitingQueueInfo fourQueue = waitingQueueService.createWaitingQueue(4);
        WaitingQueueDetail result = waitingQueueService.getWaitingQueue(fourQueue.token());

        assertThat(result.userId()).isEqualTo(4);
        assertThat(result.waitingNumber()).isEqualTo(1);
        assertThat(result.status()).isEqualTo(WaitingQueueStatus.WAITING);
    }

    @Test
    @DisplayName("대기열_조회_성공_활성상태")
    void getWaitingQueue_success_active() {
        for(long i = 0; i < 2; i++){
            WaitingQueueInfo firstInfo = waitingQueueService.createWaitingQueue(i + 1); // 대기열 생성
            waitingQueueService.getWaitingQueue(firstInfo.token());  // 생성된 대기열 active 전환
        }

        WaitingQueueInfo threeInfo = waitingQueueService.createWaitingQueue(3);
        WaitingQueueInfo fourInfo = waitingQueueService.createWaitingQueue(4);

        WaitingQueueDetail three = waitingQueueService.getWaitingQueue(threeInfo.token());
        WaitingQueueDetail four = waitingQueueService.getWaitingQueue(fourInfo.token());


        assertThat(three.userId()).isEqualTo(3);
        assertThat(three.waitingNumber()).isEqualTo(0);
        assertThat(three.remainedMillis()).isEqualTo(0);
        assertThat(three.status()).isEqualTo(WaitingQueueStatus.ACTIVE);

        assertThat(four.userId()).isEqualTo(4);
        assertThat(four.waitingNumber()).isEqualTo(1);
        assertThat(four.status()).isEqualTo(WaitingQueueStatus.WAITING);
    }

    @Test
    @DisplayName("대기열_상태_체크_성공_대기열_풀")
    void refreshWaitingQueueStatus_success_notAvailable() {
        // 활성 토큰 생성(max)
        List<WaitingQueue> activeQueue = List.of(
            WaitingQueue.create("token1", 1),
            WaitingQueue.create("token2", 2),
            WaitingQueue.create("token3", 3)
        );
        activeQueue.forEach(queue -> queue.active());
        waitingQueueRepository.saveAll(activeQueue);

        WaitingQueue four = WaitingQueue.create("token4", 4);
        WaitingQueue saved = waitingQueueRepository.save(four);

        waitingQueueService.refreshWaitingQueueStatus(LocalDateTime.now());

        //then - 4번째 아무 변화 없음
        List<WaitingQueue> result = waitingQueueRepository.findAll();

        assertThat(result).hasSize(4);
        assertThat(result).filteredOn(q -> q.getStatus() == WaitingQueueStatus.ACTIVE).hasSize(3);
        assertThat(result)
                .anyMatch(q->q.getUserId() == 4 && q.getStatus() == WaitingQueueStatus.WAITING);
    }

    @Test
    @DisplayName("대기열_상태_체크_성공_토큰만료_상태_활성화")
    void refreshWaitingQueueStatus_success() {
        LocalDateTime now = LocalDateTime.now();
        // 만료 Active 토큰 생성
        WaitingQueue q1 = WaitingQueue.create("expiredToken", 1l);
        q1.active(now.minusMinutes(11));
        // 유효 Active 토큰 생성
        WaitingQueue q2 = WaitingQueue.create("activeToken", 2l);
        q2.active();
        // 대기 토큰 생성
        WaitingQueue q3 = WaitingQueue.create("waitingToken1", 3l);
        WaitingQueue q4 = WaitingQueue.create("waitingQueue2", 4l);
        waitingQueueRepository.saveAll(List.of(q1,q2,q3,q4));

        waitingQueueService.refreshWaitingQueueStatus(now);

        // 만료 Active 토큰 -> Expired, expiredAt null
        // 유효 Active 토큰 변함 없음
        // q3 -> Active, expiredAt = now + 10
        // q4 -> 변함 없음
        List<WaitingQueue> result = waitingQueueRepository.findAll();
        assertThat(result).filteredOn(q->q.getUserId() == 1l)
                .extracting("status", "expiredAt")
                .contains(
                        tuple(WaitingQueueStatus.EXPIRED, null)
                );
        assertThat(result).filteredOn(q->q.getUserId() == 2l)
                .extracting("status", "expiredAt")
                .contains(tuple(q2.getStatus(), q2.getExpiredAt()));
        assertThat(result)
                .anyMatch(q->q.getUserId()== 3 && q.getStatus() == WaitingQueueStatus.ACTIVE);
        assertThat(result).filteredOn(q -> q.getUserId() == 4l)
                .extracting("status")
                .containsExactly(WaitingQueueStatus.WAITING);

    }
}