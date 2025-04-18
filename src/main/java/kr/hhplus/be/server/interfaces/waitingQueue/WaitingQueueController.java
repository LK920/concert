package kr.hhplus.be.server.interfaces.waitingQueue;

import kr.hhplus.be.server.application.waitingQueue.WaitingQueueFacade;
import kr.hhplus.be.server.domain.queue.WaitingQueueDetail;
import kr.hhplus.be.server.domain.queue.WaitingQueueInfo;
import kr.hhplus.be.server.interfaces.waitingQueue.request.RequestToken;
import kr.hhplus.be.server.interfaces.waitingQueue.response.ResponseQueue;
import kr.hhplus.be.server.interfaces.waitingQueue.response.ResponseToken;
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

    @PostMapping("/waiting")
    @Override
    public ResponseEntity<ResponseQueue> waitingQueue(@RequestBody RequestToken requestToken){
        WaitingQueueDetail waitingQueueDetail = waitingQueueFacade.getWaitingQueue(requestToken.token());
        ResponseQueue result = ResponseQueue.from(waitingQueueDetail);
        return ResponseEntity.ok(result);
    }

}
