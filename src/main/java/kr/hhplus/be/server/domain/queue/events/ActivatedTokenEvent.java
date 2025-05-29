package kr.hhplus.be.server.domain.queue.events;

public record ActivatedTokenEvent(
        long concertId,
        long userId,
        String token,
        long activatedAt
) {
}
