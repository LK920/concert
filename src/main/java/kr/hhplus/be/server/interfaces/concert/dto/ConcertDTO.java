package kr.hhplus.be.server.interfaces.concert;


import io.swagger.v3.oas.annotations.media.Schema;

public record ConcertResponse (
        @Schema(description = "콘서트 id", example = "1")
        long concertId,
        @Schema(description = "콘서트 이름", example = "2025MusicConcert")
        String concertName,
        @Schema(description = "콘서트 총 좌석 수 ", example = "50")
        long concertTotalSeats,
        @Schema(description = "콘서트 예약 날짜", example = "2024-05-10 11:00:00")
        String concertDate
){

}
