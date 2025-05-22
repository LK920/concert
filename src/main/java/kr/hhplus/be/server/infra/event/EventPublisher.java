package kr.hhplus.be.server.infra.event;

public interface EventPublisher {
    void publish(Object event);
}
