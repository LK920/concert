package kr.hhplus.be.server.domain.concertDate;

import java.time.LocalDateTime;

public record ConcertDateInfo(
        long concertDateId,
        LocalDateTime concertDate
) {
    public static ConcertDateInfo from(ConcertDate concertDate){
        return new ConcertDateInfo(concertDate.getId(), concertDate.getConcertDate());
    }
}
