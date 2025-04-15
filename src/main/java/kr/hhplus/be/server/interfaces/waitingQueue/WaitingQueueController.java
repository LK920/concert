package kr.hhplus.be.server.interfaces.waitingQueue;

import kr.hhplus.be.server.application.waitingQueue.WaitingQueueFacade;
import kr.hhplus.be.server.domain.queue.WaitingQueueInfo;
import kr.hhplus.be.server.interfaces.waitingQueue.dto.ResponseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class WaitingQueueController implements WaitingQueueApi {

    private final WaitingQueueFacade waitingQueueFacade;

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseToken> createWaitingQueue(@PathVariable long userId){
        WaitingQueueInfo waitingQueueInfo = waitingQueueFacade.createWaitingQueue(userId);
        ResponseToken result =  ResponseToken.of(waitingQueueInfo.userId(), waitingQueueInfo.token());
        return ResponseEntity.ok(result);
    }

    @RequestMapping("/waiting")
    @Override
    public void waitingQueue(@RequestBody long userId, String token){


    }


}
