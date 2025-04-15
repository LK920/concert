package kr.hhplus.be.server.domain.queue;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private static final long MAX_ACTIVE_USERS = 3;

    private final WaitingQueueRepository waitingQueueRepository;

    // 대기열 생성
    public WaitingQueueInfo createWaitingQueue(long userId){
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        WaitingQueue waitingQueue = WaitingQueue.create(token,userId,now);
        waitingQueueRepository.save(waitingQueue);
        return WaitingQueueInfo.from(waitingQueue);
    }

    // 대기열 조회
    // 대기열 조회
    public WaitingQueueInfo getWaitingQueue(String token){
        LocalDateTime now = LocalDateTime.now();

        WaitingQueue waitingQueue = waitingQueueRepository.findByToken(token);
        if (waitingQueue == null) {
            throw new NoSuchElementException("존재하지 않는 토큰입니다.");
        }

        // 만료 처리
        if (waitingQueue.getExpiredAt().isBefore(now)) {
            waitingQueue.isExpired(); // 대기열 만료
            waitingQueueRepository.save(waitingQueue); // 대기열 만료 상태 저장
            throw new IllegalStateException("대기열이 만료되었습니다.");
        }

        // 현재 ACTIVE 상태 사용자 수 확인
        long activeUserCount = waitingQueueRepository.countByStatus(WaitingQueueStatus.ACTIVE);

        if (activeUserCount < MAX_ACTIVE_USERS) {
            // ACTIVE 로 변경
            waitingQueue.isActived();
            waitingQueueRepository.save(waitingQueue);
            return WaitingQueueInfo.from(waitingQueue);
        } else {
            // 대기 번호 계산

            long waitingNumber = waitingQueueRepository.countByStatusAndIdLessThan(WaitingQueueStatus.WAITING, waitingQueue.getId()) + 1; // 1부터 시작하는 번호로 표시

            //수정 필요
            return WaitingQueueInfo.from(waitingQueue);
        }
    }

    public void isExpiredWaitingQueue(String token){
        // 구현할 부분: 스케줄러 돌릴 때 사용 가능
        WaitingQueue waitingQueue = waitingQueueRepository.findByToken(token);
        if (waitingQueue == null) {
            throw new NoSuchElementException("존재하지 않는 토큰입니다.");
        }

        if (waitingQueue.getExpiredAt().isBefore(LocalDateTime.now())) {
            waitingQueue.isExpired();
            waitingQueueRepository.save(waitingQueue);
        }
    }
}
