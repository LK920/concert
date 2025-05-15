package kr.hhplus.be.server.interfaces.scheduler;

import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.queue.redis.RedisWaitingQueueService;
import kr.hhplus.be.server.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final RedisWaitingQueueService waitingQueueService;
    private final RankingService rankingService;

    @Scheduled(fixedRateString = "${scheduler.fix-rate-ms}")
    public void runScheduler(){
        waitingQueueService.refreshWaitingQueueStatus();
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 00시
    public void rankingScheduler() {
        rankingService.setAggregateRanking(1, "concert:ranking:daily");
        rankingService.setAggregateRanking(7, "concert:ranking:weekly");
        rankingService.setAggregateRanking(30, "concert:ranking:monthly");
    }

}
