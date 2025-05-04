package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.application.point.PointCommand;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.interfaces.point.request.PointRequestDTO;
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

    @Override
    @PostMapping("/charge")
    public ResponseEntity<PointResponseDTO> chargeUserPoint(@RequestBody PointRequestDTO request){
        PointCommand pointCommand = pointFacade.chargePoint(request.userId(), request.amount());
        PointResponseDTO response = new PointResponseDTO(pointCommand.userId(), pointCommand.userPoint());
        return ResponseEntity.ok(response);
    }
}
