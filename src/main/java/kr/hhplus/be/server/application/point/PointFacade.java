package kr.hhplus.be.server.application.point;

import jakarta.persistence.OptimisticLockException;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;
    private final PaymentService paymentService;

    public PointCommand chargePoint(long userId, long amount){
        int maxRetryAttempt = 1;
        long retryInterval = 1000L; // 100ms 대기
        int retryAttempt = 0;

        while (retryAttempt < maxRetryAttempt){
            try{
                // 유저 포인트 충전
                PointInfo updatePointInfo = pointService.chargeUserPoint(userId,amount);
                // 내역 생성
                paymentService.createPayment(userId, amount, PaymentType.CHARGE);
                return new PointCommand(updatePointInfo.userId(), updatePointInfo.userPoint());
            }catch(ObjectOptimisticLockingFailureException e){
                // 충전 실패한 경우
                retryAttempt++;
                log.info("충전 재시도");
                try{
                    Thread.sleep(retryInterval);
                }catch (InterruptedException interruptedException){
                    throw new RuntimeException("재시도 중 인터럽트 에러 발생", interruptedException);
                }
            }catch (Exception exception){
                log.error("충전 실패 - 발생한 예외 클래스: {}", exception.getClass().getName());
                log.error("예외 메시지: {}", exception.getMessage(), exception);
                throw new RuntimeException("충전 실패", exception);
            }
        }
        throw new RuntimeException("포인트 충전 최대 횟수 초과");
    }

    public PointCommand getUserPoint(long userId){
        PointInfo pointInfo = pointService.getUserPoint(userId);
        return new PointCommand(pointInfo.userId(), pointInfo.userPoint());
    }

}
