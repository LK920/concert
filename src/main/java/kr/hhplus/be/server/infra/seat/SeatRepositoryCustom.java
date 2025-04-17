package kr.hhplus.be.server.infra.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatStatus;

import java.util.List;

public interface SeatRepositoryCustom {
    List<Seat> findByConcertDateIdAndSeatStatus(long concertDateId, SeatStatus seatStatus);
    Seat findBySeatId(long seatId);
}
