package kr.hhplus.be.server.domain.queue.events;

public record WaitingTokenEvent(
        long concertId,
        long userId,
        String token,
        long requestTime
) {
}
