package kr.hhplus.be.server.interfaces.waitingQueue.request;

public record RequestToken(
        long userId,
        String token
) {

}
