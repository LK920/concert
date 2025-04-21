package kr.hhplus.be.server.infra.concertDate;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.QConcertDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertDateRepositoryImpl implements ConcertDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConcertDate> findAllByConcertId(long concertId) {
        QConcertDate concertDate = QConcertDate.concertDate1;
        return queryFactory.selectFrom(concertDate)
                .where(concertDate.concertId.eq(concertId))
                .fetch();
    }
}
