package kr.hhplus.be.server.domain.reservation.eventHandler;

import kr.hhplus.be.server.domain.payment.events.PaymentCreatedEvent;
import kr.hhplus.be.server.domain.reservation.events.ReservationCompletedEvent;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.domain.reservation.ReservationService;
import kr.hhplus.be.server.infra.external.kafka.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final ReservationService reservationService;
    private final KafkaMessageProducer kafkaMessageProducer;
    @Async
    @EventListener
    public void handlePaymentCreated(PaymentCreatedEvent event){
        ReservationInfo updated = reservationService.updatePaymentInfo(event.reservationId(), event.paymentId());

        kafkaMessageProducer.sendReservationCompletedEvent(new ReservationCompletedEvent(
                event.userId(), event.reservationId(), event.concertId(), updated.seatId()
        ));
    }
}
