package kr.hhplus.be.server.application.reservation;

public record ReserveConcertCommand(
        long userId,
        long seatId,
        long seatPrice
){
}
