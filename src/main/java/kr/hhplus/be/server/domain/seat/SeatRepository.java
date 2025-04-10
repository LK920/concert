package kr.hhplus.be.server.domain.seat;

import java.util.List;

public interface SeatRepository {
    List<Seat> findByConcertDateIdAndSeatStatus(long concertDateId, SeatStatus seatStatus);
    Seat findBySeatId(long seatId);
    void save(Seat seat);
}
