package kr.hhplus.be.server.domain.payment.eventHandler;

import kr.hhplus.be.server.domain.events.PaymentCreatedEvent;
import kr.hhplus.be.server.domain.events.PointUsedEvent;
import kr.hhplus.be.server.domain.payment.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.infra.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentService paymentService;
    private final DomainEventPublisher publisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePointUsed(PointUsedEvent event){
        try{
            PaymentInfo paymentInfo = paymentService.createPayment(event.userId(), event.seatPrice(), PaymentType.USE);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
