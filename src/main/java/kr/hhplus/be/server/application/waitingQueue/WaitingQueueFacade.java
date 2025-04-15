package kr.hhplus.be.server.application.waitingQueue;

import kr.hhplus.be.server.domain.queue.WaitingQueue;
import kr.hhplus.be.server.domain.queue.WaitingQueueDetail;
import kr.hhplus.be.server.domain.queue.WaitingQueueInfo;
import kr.hhplus.be.server.domain.queue.WaitingQueueService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WaitingQueueFacade {

    private final WaitingQueueService waitingQueue;

    public WaitingQueueInfo createWaitingQueue(long userId){
        return waitingQueue.createWaitingQueue(userId);
    }

    public WaitingQueueDetail getWaitingQueue(String token){
        return waitingQueue.getWaitingQueue(token);
    }

}
