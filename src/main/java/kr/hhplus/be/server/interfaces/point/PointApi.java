package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.point.request.PointRequestDTO;
import kr.hhplus.be.server.interfaces.point.request.PointReservationRequest;
import kr.hhplus.be.server.interfaces.point.response.PointResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트 조회/충전 api")
@RequestMapping("/point")
public interface PointApi {

    @Operation(summary = "포인트 조회", description = "userId를 파라미터를 받아 해당 유저의 포인트를 조회한다")
    @GetMapping("/{userId}")
    default ResponseEntity<PointResponseDTO> getUserPoint(@PathVariable long userId){
        PointResponseDTO mockResponse = new PointResponseDTO(1l, 1000l);
        return ResponseEntity.ok(mockResponse);
    }

    /**
     * Charges points for a user based on the provided user ID and amount.
     *
     * @param request the request containing the user ID and amount to charge
     * @return a response entity containing the charged point information
     */
    @Operation(summary = "포인트 충전", description = "userId와 amount를 파라미터로 포인트 충전한다")
    @PostMapping("/charge")
    default ResponseEntity<PointResponseDTO> chargeUserPoint(@RequestBody PointRequestDTO request){
        PointResponseDTO mockResponse = new PointResponseDTO(request.userId(), request.amount());
        return ResponseEntity.ok(mockResponse);
    }

    /**
     * Confirms completion of a reservation payment using the provided reservation and user information.
     *
     * @param pointReservationRequest the reservation and user details for the payment
     * @return a confirmation message indicating the reservation payment is complete
     */
    @Operation(summary = "포인트 예약 결제", description = "reservationId와 userId 파라미터로 예약좌석 결제한다.")
    @PostMapping("/reservation")
    default String usePoint(@RequestBody PointReservationRequest pointReservationRequest){
        return "예약 결제가 완료 되었습니다.";
    }

}
