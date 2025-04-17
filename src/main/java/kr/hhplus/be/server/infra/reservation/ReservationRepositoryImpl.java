package kr.hhplus.be.server.infra.reservation;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.reservation.QReservation;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Reservation findByReservationId(long reservationId) {
        QReservation reservation = QReservation.reservation;
        return queryFactory.selectFrom(reservation)
                .where(reservation.id.eq(reservationId))
                .fetchOne();
    }

    @Override
    public List<Reservation> getUserReservationList(long userId) {
        QReservation reservation = QReservation.reservation;
        return queryFactory.selectFrom(reservation)
                .where(reservation.userId.eq(userId))
                .orderBy(reservation.createdAt.desc())
                .fetch();

    }

    @Override
    public List<Reservation> getReservationBySeatId(long seatId) {
        QReservation reservation = QReservation.reservation;
        return queryFactory.selectFrom(reservation)
                .where(reservation.concertSeatId.eq(seatId))
                .fetch();
    }

    @Override
    public boolean existsReservationBySeatId(long seatId){
        QReservation reservation = QReservation.reservation;
        Reservation result = queryFactory.selectFrom(reservation)
                .where(reservation.concertSeatId.eq(seatId))
                .fetchFirst();
        return result != null;
    }
}
