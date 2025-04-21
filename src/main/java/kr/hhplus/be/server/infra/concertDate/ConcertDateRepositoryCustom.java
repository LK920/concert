package kr.hhplus.be.server.infra.concertDate;

import kr.hhplus.be.server.domain.concertDate.ConcertDate;

import java.util.List;

public interface ConcertDateRepositoryCustom {
    List<ConcertDate> findAllByConcertId(long concertId);
}
