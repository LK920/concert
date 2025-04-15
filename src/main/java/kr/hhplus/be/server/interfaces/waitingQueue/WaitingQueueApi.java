package kr.hhplus.be.server.interfaces.waitingQueue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.waitingQueue.dto.ResponseQueue;
import kr.hhplus.be.server.interfaces.waitingQueue.dto.ResponseToken;
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

    @RequestMapping("/waiting")
    default ResponseEntity<ResponseQueue> waitingQueue(@RequestBody long userId, String token){

    }
}
