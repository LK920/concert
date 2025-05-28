package kr.hhplus.be.server.domain.reservation.eventHandler;

import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReservationEventConsumer {

    @KafkaListener(topics = "reservation-completed", groupId = "reservation-group")
    public void consume(ReservationCompletedEvent event, Acknowledgment acknowledgment){
        log.info("🔁 Kafka 수신됨 - 성공 알림: 유저 {}, 콘서트 {}, 예약 {}, 좌석 {}",
                event.userId(), event.concertId(), event.reservationId(), event.seatId());
        acknowledgment.acknowledge();
    }

}
