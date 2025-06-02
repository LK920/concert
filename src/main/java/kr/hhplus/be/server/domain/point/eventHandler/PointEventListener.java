package kr.hhplus.be.server.domain.point.eventHandler;

import kr.hhplus.be.server.domain.point.events.PointUsedEvent;
import kr.hhplus.be.server.domain.point.events.PointUsingFailedEvent;
import kr.hhplus.be.server.domain.reservation.events.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.infra.event.DomainEventPublisher;
import kr.hhplus.be.server.infra.external.kafka.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {
    private final PointService pointService;
    private final DomainEventPublisher publisher;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationCompleted(ReservationCreatedEvent event){
        try{
            pointService.useUserPoint(event.userId(), event.seatPrice());
            // 포인트 차감 성공 후 이벤트 발행
            publisher.publish(new PointUsedEvent(
                    event.userId(),
                    event.reservationId(),
                    event.concertId(),
                    event.seatId(),
                    event.seatPrice()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("[포인트 사용 실패] - 메시지 : {}", e.getMessage());
            // 포인트 차감 실패 후 보상 이벤트 발행 유저에게 재 결제 알림 전달
            kafkaMessageProducer.sendPointFailureEvent(new PointUsingFailedEvent(
                    event.userId(),
                    event.concertId(),
                    event.reservationId(),
                    event.seatId(),
                    "포인트 충전 후 예약 결제를 진행해주세요"
            ));
        }
    }
}
