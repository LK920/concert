package kr.hhplus.be.server.domain.point.eventHandler;

import kr.hhplus.be.server.domain.point.events.PointUsingFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PointEventConsumer {

    @KafkaListener(topics = "point-usage-failed", groupId = "notification-group")
    public void consume(PointUsingFailedEvent event, Acknowledgment acknowledgment){

        log.info("🔁 Kafka 수신됨 - 실패 알림: 유저 {}, 콘서트 {}, 좌석 {}, 사유: {}",
                event.userId(), event.concertId(), event.seatId(), event.reason());
        acknowledgment.acknowledge();
    }

}
