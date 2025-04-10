package kr.hhplus.be.server.application.reservation;

public record ReservationCommand (
        long userId,
        long seatId,
        long seatPrice
){
}
