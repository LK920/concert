package kr.hhplus.be.server.domain.point.eventHandler;

import kr.hhplus.be.server.domain.events.PointUsedEvent;
import kr.hhplus.be.server.domain.events.PointUsingFailedEvent;
import kr.hhplus.be.server.domain.events.ReservationCreatedEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.infra.event.DomainEventPublisher;
import kr.hhplus.be.server.infra.external.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {
    private final PointService pointService;
    private final DataPlatformClient dataPlatformClient;
    private final DomainEventPublisher publisher;

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
        } catch (Exception e) {
            log.warn("[포인트 사용 실패] - 메시지 : {}", e.getMessage());
            // 포인트 차감 실패 후 보상 이벤트 발행 유저에게 재 결제 알림 전달
            publisher.publish(new PointUsingFailedEvent(
                    event.userId(),
                    event.concertId(),
                    event.reservationId(),
                    event.seatId(),
                    "포인트 충전 후 예약 결제를 진행해주세요"
            ));
        }
    }

    @Async
    @EventListener
    public void handlePointUsingFailed(PointUsingFailedEvent event){
        // 유저에게 포인트 결제 실패하여 예약하라고 전달
        log.warn("[이벤트] 예약 실패 - 유저 {}, 좌석 {}, 사유: {}",
                event.userId(), event.seatId(), event.reason());
    }

}
