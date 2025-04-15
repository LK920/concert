package kr.hhplus.be.server.infra.jpa;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SeatRespositoryImpl implements SeatRepository {
    @Override
    public List<Seat> findByConcertDateIdAndSeatStatus(long concertDateId, SeatStatus seatStatus) {
        return List.of();
    }

    @Override
    public Seat findBySeatId(long seatId) {
        return null;
    }

    @Override
    public void save(Seat seat) {

    }
}
