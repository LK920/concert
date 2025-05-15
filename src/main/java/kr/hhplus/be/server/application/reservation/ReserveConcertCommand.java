package kr.hhplus.be.server.application.reservation;

public record ReserveConcertCommand(
        long concertId,
        long userId,
        long seatId,
        long seatPrice
){
}
