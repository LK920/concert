package kr.hhplus.be.server.domain.queue.events;

public record UserNotifiedEvent(
        long userId,
        String token,
        long notifiedAt
) {
}
