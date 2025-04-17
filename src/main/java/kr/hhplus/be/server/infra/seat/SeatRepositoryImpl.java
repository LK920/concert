package kr.hhplus.be.server.infra.seat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.seat.QSeat;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.SeatRepository;
import kr.hhplus.be.server.domain.seat.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Seat> findByConcertDateIdAndSeatStatus(long concertDateId, SeatStatus seatStatus) {
        QSeat seat = QSeat.seat;
        return queryFactory.selectFrom(seat)
                .where(
                        seat.concertDateId.eq(concertDateId),
                        seat.seatStatus.eq(seatStatus)
                ).fetch();
    }

    @Override
    public Seat findBySeatId(long seatId) {
        QSeat seat = QSeat.seat;
        return queryFactory.selectFrom(seat)
                .where(seat.id.eq(seatId))
                .fetchOne();
    }

}
