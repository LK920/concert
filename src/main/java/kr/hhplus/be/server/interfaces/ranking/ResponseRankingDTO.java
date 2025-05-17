package kr.hhplus.be.server.interfaces.ranking;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.ranking.Ranking;
import kr.hhplus.be.server.interfaces.concert.response.ConcertResponseDTO;

public record ResponseRankingDTO(
        long rank,
        long concertId,
        String concertName
) {
    public static ResponseRankingDTO from(Ranking ranking){
        return new ResponseRankingDTO(ranking.rank(), ranking.concertId(), ranking.concertName());
    }
}
