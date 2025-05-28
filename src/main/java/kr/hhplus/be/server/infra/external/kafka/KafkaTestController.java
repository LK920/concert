package kr.hhplus.be.server.infra.external.kafka;

import kr.hhplus.be.server.domain.point.events.PointUsingFailedEvent;
import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaTestController {

    private final KafkaMessageProducer producer;

    @GetMapping("/send")
    public ResponseEntity<Void> sendMessage() {
        producer.sendReservationCompletedEvent(new ReservationCompletedEvent(
                1l,1l,1l,1
        ));

        producer.sendPointFailureEvent(new PointUsingFailedEvent(
                2l,2l,2l,2l,"포인트 추가 충전"
        ));
        return ResponseEntity.ok().build();
    }

}
