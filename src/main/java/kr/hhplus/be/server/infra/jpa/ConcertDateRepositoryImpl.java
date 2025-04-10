package kr.hhplus.be.server.infra.jpa;

import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import kr.hhplus.be.server.domain.concertDate.ConcertDateRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ConcertDateRepositoryImpl implements ConcertDateRepository {
    @Override
    public List<ConcertDate> findAllByConcertIdAndIsAvailableTrue(long concertId) {
        return List.of();
    }
}
