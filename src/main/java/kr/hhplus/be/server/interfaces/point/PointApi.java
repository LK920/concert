package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.point.request.PointRequestDTO;
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

    @Operation(summary = "포인트 충전", description = "userId와 amount를 파라미터로 포인트 충전한다")
    @PostMapping("/charge")
    default ResponseEntity<PointResponseDTO> chargeUserPoint(@RequestBody PointRequestDTO request){
        PointResponseDTO mockResponse = new PointResponseDTO(request.userId(), request.amount());
        return ResponseEntity.ok(mockResponse);
    }
}
