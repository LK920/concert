package kr.hhplus.be.server.interfaces.concert;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.concert.response.ConcertResponseDTO;
import kr.hhplus.be.server.interfaces.concert.response.ConcertDateResponseDTO;
import kr.hhplus.be.server.interfaces.concert.response.ConcertSeatResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name="콘서트 api", description = "콘서트 조회, 날짜를 조회하는 역할을 한다.") // @Tag 설정된 name이 같은 것 끼리 하나의 api 그룹으로 묶음
@RequestMapping("/concert")
public interface ConcertApi {

    @Operation(summary = "예약 가능한 콘서트를 조회 api")
    @GetMapping("/list")
    default ResponseEntity<Page<ConcertResponseDTO>> getConcerts(Pageable pageable){
        List<ConcertResponseDTO> mockData = List.of(
            new ConcertResponseDTO(1l, "concert 1", 50l),
            new ConcertResponseDTO(2l, "concert 2", 50l)
        );
        Page<ConcertResponseDTO> mockApi = new PageImpl<>(mockData, pageable,mockData.size());
     return ResponseEntity.ok(mockApi);
    }

    @Operation(summary = "예약 가능한 콘서트 날짜 조회 api", description = "파라미터로 concertId를 받는다")
    @GetMapping("/{concertId/date}")
    default ResponseEntity<List<ConcertDateResponseDTO>> getConcertAvailableDates(@PathVariable long concertId){
        List<ConcertDateResponseDTO> mockConcertDateList = List.of(
               new ConcertDateResponseDTO(1l, "2025-05-01 22:00:00"),
                new ConcertDateResponseDTO(2l, "2025-05-02 22:00:00")
        );

        return ResponseEntity.ok(mockConcertDateList);
    }

    @Operation(summary = "예약 가능한 콘서트 좌석 조회 api", description = "파라미터로 dateId 받는다")
    @GetMapping("/{dateId/seat}")
    default ResponseEntity<List<ConcertSeatResponseDTO>> getConcertAvailableSeats(@PathVariable long dateId){
        List<ConcertSeatResponseDTO> mockConcertList = List.of(
                new ConcertSeatResponseDTO(1l, 1l, 20000l),
                new ConcertSeatResponseDTO(2l, 2l, 20000l)
        );

        return ResponseEntity.ok(mockConcertList);
    }

}
