package kr.hhplus.be.server.domain.concert;

import kr.hhplus.be.server.infra.concert.ConcertRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long>, ConcertRepositoryCustom {
    /*
    * Jpa, querydsl 메서드 사용 가능
    * */
}
