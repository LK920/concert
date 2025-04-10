package kr.hhplus.be.server.interfaces.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.reservation.request.ReserveRequestDTO;
import kr.hhplus.be.server.interfaces.reservation.response.ReserveResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "콘서트 예약 / 예약 내역 조회 api")
@RequestMapping("/reservation")
public interface ReservationApi {

    @Operation(summary = "콘서트를 예약한다", description = "콘서트 리퀘스트 객체를 받아 콘서트 예약을 한다.")
    @PostMapping("/reserve")
    default ResponseEntity<ReserveResponseDTO> reserveConcert(@RequestBody ReserveRequestDTO reqeust){
        ReserveResponseDTO result = new ReserveResponseDTO(1l, 1l, 1l, "COMPLETE");
        return ResponseEntity.ok(result);
    }

}
