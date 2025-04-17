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
    public Page<Concert> findAllConcerts(Pageable pageable) {
        QConcert concert = QConcert.concert;
        List<Concert> content = queryFactory
                .selectFrom(concert)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(concert.id.desc())
                .fetch();
        Long total = queryFactory
                .select(concert.count())
                .from(concert)
                .fetchOne();
        return new PageImpl<>(content,pageable,total);
    }

}
