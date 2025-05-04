package kr.hhplus.be.server.interfaces.concert;

import kr.hhplus.be.server.domain.concert.ConcertInfo;
import kr.hhplus.be.server.domain.concert.ConcertService;
import kr.hhplus.be.server.domain.concertDate.ConcertDateInfo;
import kr.hhplus.be.server.domain.concertDate.ConcertDateService;
import kr.hhplus.be.server.domain.seat.SeatInfo;
import kr.hhplus.be.server.domain.seat.SeatService;
import kr.hhplus.be.server.interfaces.concert.response.ConcertResponseDTO;
import kr.hhplus.be.server.interfaces.concert.response.ConcertDateResponseDTO;
import kr.hhplus.be.server.interfaces.concert.response.ConcertSeatResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/concert")
@RequiredArgsConstructor
public class ConcertController implements ConcertApi{

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertDateService concertDateService;

    @Autowired
    private SeatService seatService;

    @GetMapping("/list")
    @Override
    public ResponseEntity<List<ConcertResponseDTO>> getConcerts(){
        List<ConcertInfo> concerts = concertService.getConcerts();
        List<ConcertResponseDTO> result = concerts.stream()
                .map(concertInfo -> ConcertResponseDTO.from(concertInfo)
                ).toList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{concertId}/date")
    @Override
    public ResponseEntity<List<ConcertDateResponseDTO>> getConcertAvailableDates(@PathVariable("concertId") long concertId){
        List<ConcertDateInfo> concertDateInfos = concertDateService.getConcertAvailableDates(concertId);
        List<ConcertDateResponseDTO> result = concertDateInfos.stream()
                .map(concertDateInfo -> ConcertDateResponseDTO.from(concertDateInfo)).toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{concertDateId}/seat")
    @Override
    public ResponseEntity<List<ConcertSeatResponseDTO>> getConcertAvailableSeats(@PathVariable("concertDateId") long concertDateId){
        List<SeatInfo> seatInfos = seatService.getAvailableSeats(concertDateId);
        List<ConcertSeatResponseDTO> result = seatInfos.stream()
                .map( seatInfo-> ConcertSeatResponseDTO.from(seatInfo)
                ).toList();
        return ResponseEntity.ok(result);
    }
}
