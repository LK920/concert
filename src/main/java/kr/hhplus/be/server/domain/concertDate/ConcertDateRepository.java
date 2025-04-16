package kr.hhplus.be.server.domain.concertDate;

import kr.hhplus.be.server.infra.concertDate.ConcertDateRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertDateRepository extends JpaRepository<ConcertDate, Long>, ConcertDateRepositoryCustom {

}
