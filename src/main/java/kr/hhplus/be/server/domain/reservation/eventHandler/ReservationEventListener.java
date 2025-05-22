package kr.hhplus.be.server.domain.reservation.eventHandler;

import kr.hhplus.be.server.domain.DataPlatformService;
import kr.hhplus.be.server.domain.events.PaymentCreatedEvent;
import kr.hhplus.be.server.domain.events.ReservationCompletedEvent;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
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
public class ReservationEventListener {
    private final ReservationService reservationService;
    private final DataPlatformService dataPlatformService;

    @Async
    @EventListener
    public void handlePaymentCreated(PaymentCreatedEvent event){
        ReservationInfo updated = reservationService.updatePaymentInfo(event.reservationId(), event.paymentId());
        log.info("[예약 결제 확정] reservationId : {}", event.reservationId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendToReservationCompleted(ReservationCompletedEvent event){
        dataPlatformService.process(event);
    }
}
