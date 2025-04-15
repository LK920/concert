package kr.hhplus.be.server.interfaces.waitingQueue.dto;

public record ResponseToken(
        long userId,
        String token
) {
    public static ResponseToken of(long userId, String token){
        return new ResponseToken(userId,token);
    }
}
