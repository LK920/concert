package kr.hhplus.be.server.domain.seat;

public record SeatInfo(
        long seatId,
        long seatNumber,
        long seatPrice
) {
    public static SeatInfo from(Seat seat){
        return new SeatInfo(seat.getId(), seat.getConcertSeatNumber(), seat.getConcertSeatPrice());
    }
}
