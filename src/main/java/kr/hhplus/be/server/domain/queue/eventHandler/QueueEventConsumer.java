package kr.hhplus.be.server.domain.queue.eventHandler;

import kr.hhplus.be.server.domain.queue.WaitingQueueKafkaService;
import kr.hhplus.be.server.domain.queue.events.ActivatedTokenEvent;
import kr.hhplus.be.server.domain.queue.events.WaitingTokenEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueueEventConsumer {

    private final WaitingQueueKafkaService waitingQueueKafkaService;

    private final Map<Long, Queue<WaitingTokenEvent>> waitingQueueMap = new ConcurrentHashMap<>();
    private static final int MAX_ACTIVATE_COUNT = 500;

    @KafkaListener(topics = "activated-token", groupId = "activated-group")
    public void activatedTokenConsume(ActivatedTokenEvent event, Acknowledgment ack){
        waitingQueueKafkaService.notifyUser(event);

        System.out.println("토큰 활성화: " + event.token());
        ack.acknowledge();
    }
    @KafkaListener(topics = "waiting-token", groupId = "waiting-group")
    public void onWaitingTokenReceive(WaitingTokenEvent event, Acknowledgment ack){
        log.info("대기열 토큰 수신: {}", event);

        // 콘서트별 큐
        waitingQueueMap.computeIfAbsent(event.concertId(), k -> new ConcurrentLinkedQueue<>()).add(event);
        ack.acknowledge();
    }

    @Scheduled(fixedRate = 10000)
    public void activateTokens(){
        for (Map.Entry<Long, Queue<WaitingTokenEvent>> entry : waitingQueueMap.entrySet()){
            Long concertId = entry.getKey();
            Queue<WaitingTokenEvent> queue = entry.getValue();

            int count = 0;

            while (!queue.isEmpty() && count < MAX_ACTIVATE_COUNT){
                WaitingTokenEvent event = queue.poll();
                if(event != null){
                    waitingQueueKafkaService.publishActivatedToken(event);
                    count++;
                }
            }
        }
    }
}
