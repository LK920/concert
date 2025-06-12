package kr.hhplus.be.server.infra.external.kafka;

import kr.hhplus.be.server.domain.point.events.PointUsingFailedEvent;
import kr.hhplus.be.server.domain.queue.events.ActivatedTokenEvent;
import kr.hhplus.be.server.domain.queue.events.UserNotifiedEvent;
import kr.hhplus.be.server.domain.queue.events.WaitingTokenEvent;
import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void send(String topic, String key, T payload) {
        kafkaTemplate.send(topic, key, payload);
    }

    public void sendPointFailureEvent(PointUsingFailedEvent event) {
        send("point-usage-failed", String.valueOf(event.userId()), event);
    }

    public void sendReservationCompletedEvent(ReservationCompletedEvent event) {
        send("reservation-completed", String.valueOf(event.reservationId()), event);
    }

    public void sendWaitingToken(WaitingTokenEvent event){
        send("waiting-token", String.valueOf(event.concertId()), event);
    }

    public void sendActivatedToken(ActivatedTokenEvent event){
        send("activated-token", String.valueOf(event.concertId()), event);
    }

    public void sendNotification(UserNotifiedEvent event){
        log.info("활성화 유저id : {}", event.userId());
        send("notification", String.valueOf(event.userId()), event);
    }

}
