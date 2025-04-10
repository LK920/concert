package kr.hhplus.be.server.interfaces.concert.dto;


import io.swagger.v3.oas.annotations.media.Schema;

public record ConcertResponseDTO(
        @Schema(description = "콘서트 id", example = "1")
        long concertId,
        @Schema(description = "콘서트 이름", example = "2025MusicConcert")
        String concertName,
        @Schema(description = "콘서트 총 좌석 수 ", example = "50")
        long concertTotalSeats
){

}
