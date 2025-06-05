package kr.hhplus.be.server.domain.queue;

import kr.hhplus.be.server.domain.queue.events.ActivatedTokenEvent;
import kr.hhplus.be.server.domain.queue.events.UserNotifiedEvent;
import kr.hhplus.be.server.domain.queue.events.WaitingTokenEvent;
import kr.hhplus.be.server.infra.external.kafka.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueKafkaService {

    private final KafkaMessageProducer kafkaMessageProducer;

    // 대기열 이벤트 발행
    public void createWaitingQueue(long userId, long concertId){
        String token = UUID.randomUUID().toString();
        kafkaMessageProducer.sendWaitingToken(new WaitingTokenEvent(
                concertId, userId, token, System.currentTimeMillis()
        ));
    }

    public void publishActivatedToken(WaitingTokenEvent waitingTokenEvent){
        ActivatedTokenEvent event = new ActivatedTokenEvent(
                waitingTokenEvent.concertId(),
                waitingTokenEvent.userId(),
                waitingTokenEvent.token(),
                System.currentTimeMillis()
        );
        kafkaMessageProducer.sendActivatedToken(event);
    }

    public void notifyUser(ActivatedTokenEvent activatedTokenEvent){
        UserNotifiedEvent event = new UserNotifiedEvent(
                activatedTokenEvent.userId(),
                activatedTokenEvent.token(),
                System.currentTimeMillis()
        );

        kafkaMessageProducer.sendNotification(event);
    }

}
