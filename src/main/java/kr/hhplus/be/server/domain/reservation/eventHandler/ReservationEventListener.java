package kr.hhplus.be.server.domain.reservation.eventHandler;

import kr.hhplus.be.server.domain.events.PaymentCreatedEvent;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.infra.external.DataPlatformClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final ReservationService reservationService;
    private final DataPlatformClient dataPlatformClient;

    @Async
    @EventListener
    public void handlePaymentCreated(PaymentCreatedEvent event){
        ReservationInfo updated = reservationService.updatePaymentInfo(event.reservationId(), event.paymentId());
        dataPlatformClient.sendReservation(updated);
    }
}
