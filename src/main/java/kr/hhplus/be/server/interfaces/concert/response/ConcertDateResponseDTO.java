package kr.hhplus.be.server.interfaces.concert.response;

import kr.hhplus.be.server.domain.concertDate.ConcertDateInfo;

import java.time.format.DateTimeFormatter;

public record ConcertDateResponseDTO(
        long concertDateId,
        String concertDate
){
    public static ConcertDateResponseDTO from(ConcertDateInfo info) {
        long concertDateId = info.concertDateId();
        String formattedDate = info.concertDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new ConcertDateResponseDTO(concertDateId, formattedDate);
    }
}
