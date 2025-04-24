package kr.hhplus.be.server.infra.concert;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.QConcert;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Concert> findAllConcerts() {
        QConcert concert = QConcert.concert;
        List<Concert> result = queryFactory
                .selectFrom(concert)
                .orderBy(concert.id.desc())
                .fetch();
        return result;
    }

}
