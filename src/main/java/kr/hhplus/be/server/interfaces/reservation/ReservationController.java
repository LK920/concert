package kr.hhplus.be.server.interfaces.reservation;

import kr.hhplus.be.server.application.reservation.ReservationFacade;
import kr.hhplus.be.server.application.reservation.ReserveConcertCommand;
import kr.hhplus.be.server.domain.reservation.ReservationInfo;
import kr.hhplus.be.server.interfaces.reservation.request.ReserveRequestDTO;
import kr.hhplus.be.server.interfaces.reservation.response.ReserveResponseDTO;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi{
    private final ReservationFacade reservationFacade;

    @Override
    @PostMapping("/reserve")
    public ResponseEntity<ReserveResponseDTO> reserveConcert(@RequestBody ReserveRequestDTO reqeust){
        ReserveConcertCommand command = new ReserveConcertCommand(reqeust.concertId(), reqeust.userId(), reqeust.seatId(), reqeust.seatPrice());
        ReservationInfo reservationInfo = reservationFacade.reserveConcert(command);
        ReserveResponseDTO result = new ReserveResponseDTO(reservationInfo.reservationId(), reservationInfo.userId(), reservationInfo.seatId(), reservationInfo.reservationStatus().toString());
        return ResponseEntity.ok(result);
    }
}
