package kr.hhplus.be.server.domain.concert;

public record ConcertInfo(
        long concertId,
        String concertName,
        long concertTotalSeat
) {
    public static ConcertInfo from(Concert concert){
        return new ConcertInfo(concert.getId(), concert.getConcertName().getConcertName(), concert.getConcertTotalSeat().getConcertTotalSeat());
    }
}
