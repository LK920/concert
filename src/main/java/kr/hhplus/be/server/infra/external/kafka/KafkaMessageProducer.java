package kr.hhplus.be.server.infra.external.kafka;

import kr.hhplus.be.server.domain.point.events.PointUsingFailedEvent;
import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

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

}
