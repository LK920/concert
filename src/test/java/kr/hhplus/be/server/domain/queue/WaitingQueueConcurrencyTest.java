package kr.hhplus.be.server.domain.queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
public class WaitingQueueConcurrencyTest {
    @Autowired
    private WaitingQueueService waitingQueueService;
    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @Test
    @DisplayName("대기열_선점_동시성_테스트")
    void active_waiting_queue_concurrency() throws InterruptedException {
        // 여러 유저가 대기열 선점 시도
        int userCnt = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(20);

        CountDownLatch starLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(userCnt);
        // 대기열 미리 생성
        for(long i = 0 ; i < userCnt; i++){
            waitingQueueService.createWaitingQueue(i+1);
        }

        List<WaitingQueue> waitingList = waitingQueueRepository.findAll();
        List<String> tokens = waitingList.stream().map(queue-> queue.getToken()).toList();

        for(String token : tokens){
            executorService.submit(()->{
               try {
                   starLatch.await();
                   waitingQueueService.getWaitingQueue(token);
               } catch (Exception e) {
                   log.error(e.getMessage());
               }finally {
                   endLatch.countDown();
               }
            });
        }

        starLatch.countDown();
        endLatch.await();
        executorService.shutdown();

        List<WaitingQueue> activeList = waitingQueueRepository.findByStatus(WaitingQueueStatus.ACTIVE);
        List<WaitingQueue> waitingQueueList = waitingQueueRepository.findByStatus(WaitingQueueStatus.WAITING);

        assertThat(activeList).hasSize(3);
        assertThat(waitingQueueList).hasSize(userCnt - 3);
    }

}
