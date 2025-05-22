package kr.hhplus.be.server.infra.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Object event) {
        String className = event.getClass().getName();
        log.info("{} 이벤트 발행 시작.....", className);
        applicationEventPublisher.publishEvent(event);
        log.info("{} 이벤트 발행 종료.....",className);
    }
}
