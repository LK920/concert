package kr.hhplus.be.server.interfaces.scheduler;

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

    @Scheduled(fixedRateString = "${scheduler.fix-rate-ms}")
    public void runScheduler(){
        waitingQueueService.refreshWaitingQueueStatus(LocalDateTime.now());
    }
}
