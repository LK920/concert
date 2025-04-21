package kr.hhplus.be.server.application.scheduler;

import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final WaitingQueueService waitingQueueService;

    @Transactional
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void runScheduler(){
        waitingQueueService.refreshWaitingQueueStatus(LocalDateTime.now());
    }
}
