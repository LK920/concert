package kr.hhplus.be.server.infra.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concertDate.ConcertDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConcertRepositoryCustom {
    List<Concert> findAllConcerts();
    List<Concert> findAllByIdIn(List<Long> concertIds);
}
