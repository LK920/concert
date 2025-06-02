package kr.hhplus.be.server.domain.payment.eventHandler;

import kr.hhplus.be.server.domain.payment.events.PaymentCreatedEvent;
import kr.hhplus.be.server.domain.point.events.PointUsedEvent;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.infra.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentService paymentService;
    private final DomainEventPublisher publisher;

    @Async
    @EventListener
    public void handlePointUsed(PointUsedEvent event){
        PaymentInfo paymentInfo = paymentService.createPayment(event.userId(), event.seatPrice(), PaymentType.USE);
        publisher.publish(new PaymentCreatedEvent(event.userId(), event.concertId(), event.reservationId(), paymentInfo.paymentId()));
    }
}
