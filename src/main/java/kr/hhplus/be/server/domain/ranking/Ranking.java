package kr.hhplus.be.server.domain.ranking;

public record Ranking(
        long rank,
        long concertId,
        String concertName
) {
}
