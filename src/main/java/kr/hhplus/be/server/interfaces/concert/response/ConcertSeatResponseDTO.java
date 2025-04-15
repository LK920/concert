package kr.hhplus.be.server.interfaces.concert.response;

import kr.hhplus.be.server.domain.seat.SeatInfo;

public record ConcertSeatResponseDTO(
        long concertSeatId,
        long concertSeatNumber,
        long concertSeatPrice
) {
    public static ConcertSeatResponseDTO from(SeatInfo seatInfo){
        return new ConcertSeatResponseDTO(seatInfo.seatId(), seatInfo.seatNumber(), seatInfo.seatPrice());
    }
}
