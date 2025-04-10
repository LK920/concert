package kr.hhplus.be.server.domain.concertDate;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertDateRepository {
    List<ConcertDate> findAllByConcertIdAndIsAvailableTrue(long concertId);
}
