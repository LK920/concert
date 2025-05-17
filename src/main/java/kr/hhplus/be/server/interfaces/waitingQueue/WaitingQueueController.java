package kr.hhplus.be.server.interfaces.waitingQueue;

import kr.hhplus.be.server.domain.queue.redis.RedisQueueInfo;
import kr.hhplus.be.server.domain.queue.redis.RedisQueueStatusResponse;
import kr.hhplus.be.server.domain.queue.redis.RedisWaitingQueueService;
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

    private final RedisWaitingQueueService waitingQueueService;

    @Override
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseToken> createWaitingQueue(@PathVariable("userId") long userId){
        RedisQueueInfo waitingQueueInfo = waitingQueueService.createWaitingQueue(userId);
        ResponseToken result =  ResponseToken.of(waitingQueueInfo.userId(), waitingQueueInfo.token());
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/waiting")
    public ResponseEntity<ResponseQueue> waitingQueue(@RequestBody RequestToken requestToken){
        RedisQueueStatusResponse waitingQueueDetail = waitingQueueService.getWaitingQueue(requestToken.token());
        ResponseQueue result = ResponseQueue.fromRedis(waitingQueueDetail);
        return ResponseEntity.ok(result);
    }

}
