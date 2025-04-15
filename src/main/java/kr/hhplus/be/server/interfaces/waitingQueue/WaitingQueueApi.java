package kr.hhplus.be.server.interfaces.waitingQueue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.queue.WaitingQueueDetail;
import kr.hhplus.be.server.domain.queue.WaitingQueueStatus;
import kr.hhplus.be.server.interfaces.waitingQueue.request.RequestToken;
import kr.hhplus.be.server.interfaces.waitingQueue.response.ResponseQueue;
import kr.hhplus.be.server.interfaces.waitingQueue.response.ResponseToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대기열 등록 / 대기번호 조회 api")
@RequestMapping("/queue")
public interface WaitingQueueApi {

    @Operation(summary = "대기열 등록", description = "토큰을 발급하여 대기열에 등록한다")
    @GetMapping("/{userId}")
    default ResponseEntity<ResponseToken> createWaitingQueue(@PathVariable long userId){
        String token = "token-uuid";
        return ResponseEntity.ok(ResponseToken.of(userId,token));
    }

    @Operation(summary = "대기열 조회", description = "RequestToken으로 받아서 token을 가지고 대기열을 조회한다")
    @PostMapping("/waiting")
    default ResponseEntity<ResponseQueue> waitingQueue(@RequestBody RequestToken requestToken) {
        WaitingQueueDetail waitingQueueDetail = new WaitingQueueDetail(1l, 2l, 1, "token-uuid", WaitingQueueStatus.WAITING, 1l);
        ResponseQueue result = ResponseQueue.from(waitingQueueDetail);
        return ResponseEntity.ok(result);
    }
}
