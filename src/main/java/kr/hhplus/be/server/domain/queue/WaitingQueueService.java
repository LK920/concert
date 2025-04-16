package kr.hhplus.be.server.domain.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private static final long MAX_ACTIVE_USERS = 3;

    private final WaitingQueueRepository waitingQueueRepository;

    // 대기열 생성
    public WaitingQueueInfo createWaitingQueue(long userId){
        String token = UUID.randomUUID().toString();
        WaitingQueue waitingQueue = WaitingQueue.create(token,userId);
        WaitingQueue saved = waitingQueueRepository.save(waitingQueue);
        return WaitingQueueInfo.from(saved);
    }

    // 대기열 조회
    public WaitingQueueDetail getWaitingQueue(String token){
        LocalDateTime now = LocalDateTime.now();

        WaitingQueue waitingQueue = waitingQueueRepository.findByToken(token).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 토큰입니다."));

        // 현재 ACTIVE 상태 사용자 수 확인
        long activeUserCount = waitingQueueRepository.countByStatus(WaitingQueueStatus.ACTIVE);

        // 참가 가능한 유저 수 체크
        if (waitingQueue.isActivated(activeUserCount, MAX_ACTIVE_USERS)) {
            waitingQueue.active();
            waitingQueueRepository.save(waitingQueue);
            return WaitingQueueDetail.from(waitingQueue,0, 0);
        }

        // 대기 번호 계산
        // 웨이팅 상태인 토큰 들 중 조회하는 토큰의 생성일자보다 빨리 등록된 토큰 수 조회
        // 생성 토큰 중에서 가장 오래된 토큰 조회
        // 대기 시간은 가장 오래된 토큰의 남은 시간
        long waitingNumber = waitingQueueRepository.findWaitingNumber(token); // 1부터 시작하는 번호로 표시
        Optional<WaitingQueue> oldestQueue = waitingQueueRepository.findOldestActiveWaiting();
        long remainedMillis = oldestQueue
                .map(q -> Duration.between(now, q.getExpiredAt()).toMillis())
                .orElse(0L);
        return WaitingQueueDetail.from(waitingQueue,waitingNumber, remainedMillis);
    }

    // 스케줄러
    // 대기열 토큰 조회
    public void refreshWaitingQueueStatus(LocalDateTime now){
        // 활성 토큰 조회
        // 활성 토큰 만료시간 체크
        // 활성 토큰 빈자리 개수(= 들어올 수 있는 대기인원 수)
        // 빈 자리 수를 바탕으로 대기인원 수 조회(limit 빈자리 수)
        // 대기인원의 상태를 활성 상태로 변경, 만료시간 설정

        // 1. 만료된 ACTIVE 토큰 조회 및 만료 처리
        List<WaitingQueue> activeTokens = waitingQueueRepository.findByStatus(WaitingQueueStatus.ACTIVE);

        if(activeTokens.isEmpty()){
            log.info("만료 처리할 토큰이 없습니다.");
            return ;
        }

        activeTokens.stream()
                .filter(token -> token.isExpired(now)) // 만료조건 만족한 토큰만 걸러냄
                .forEach(WaitingQueue::expire); // 만족한 토큰들은 모두 만료 처리

        // 2. 현재 ACTIVE 상태 수 재계산
        long currentActiveCount = activeTokens.stream()
                .filter(token -> !token.isExpired(now)) // 아직 유효한 ACTIVE만 카운트
                .count();

        // 3. 빈 자리 계산
        long availableSlots = MAX_ACTIVE_USERS - currentActiveCount;
        if (availableSlots <= 0) {
            return; // 자리가 없으면 처리 종료
        }

        // 4. 대기열에서 오래된 WAITING 상태 유저들 조회 (limit by availableSlots)
        List<WaitingQueue> waitingList = waitingQueueRepository.findOldestWaiting((int) availableSlots);
        if(waitingList.isEmpty()){
            log.info("대기 중인 토큰이 없습니다.");
            return;
        }

        // 5. 이들을 ACTIVE 상태로 전환
        waitingList.forEach(WaitingQueue::active);

        // 6. 변경된 엔티티들 저장
        waitingQueueRepository.saveAll(activeTokens); // expire된 것 포함
        waitingQueueRepository.saveAll(waitingList);  // 새로 활성화된 것
    }
}
