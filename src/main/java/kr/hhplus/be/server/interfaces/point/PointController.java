package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.application.point.PointCommand;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.interfaces.point.request.PointRequestDTO;
import kr.hhplus.be.server.interfaces.point.request.PointReservationRequest;
import kr.hhplus.be.server.interfaces.point.response.PointResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController implements PointApi {

    private final PointFacade pointFacade;

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<PointResponseDTO> getUserPoint(@PathVariable("userId") long userId){
        PointCommand pointCommand = pointFacade.getUserPoint(userId);
        PointResponseDTO response = new PointResponseDTO(pointCommand.userId(), pointCommand.userPoint());
        return ResponseEntity.ok(response);
    }

    /**
     * Charges points to a user's account and returns the updated point balance.
     *
     * @param request the request containing the user ID and the amount of points to charge
     * @return a response entity containing the user's updated point information
     */
    @Override
    @PostMapping("/charge")
    public ResponseEntity<PointResponseDTO> chargeUserPoint(@RequestBody PointRequestDTO request){
        PointCommand pointCommand = pointFacade.chargePoint(request.userId(), request.amount());
        PointResponseDTO response = new PointResponseDTO(pointCommand.userId(), pointCommand.userPoint());
        return ResponseEntity.ok(response);
    }

    /**
     * Handles a POST request to confirm payment for a reserved concert using points.
     *
     * @param pointReservationRequest the reservation details provided in the request body
     * @return a confirmation message indicating the concert was paid for with points
     */
    @Override
    @PostMapping("/reservation")
    public String usePoint(@RequestBody PointReservationRequest pointReservationRequest) {
        return "예약한 콘서트를 포인트로 결제하였습니다.";
    }
}
