package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.support.redis.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;
    private final PaymentService paymentService;
    private final RedisLock redisLock;

    public PointCommand chargePoint(long userId, long amount){
        String lockKey = "lock:point:" + userId;
        String lockValue = "lock:point:" + UUID.randomUUID().toString();
//      심플 락 획득 -> 포인트 충전은 1번만 실행되면 된다.
        boolean getLock = redisLock.tryLock(lockKey, lockValue);
        if(getLock){
            try{
                log.info(lockKey + "키의 락을 획득합니다.");
                return chargePointTransactional(userId, amount);
            } catch (Exception e) {
                log.error("포인트 충전에 실패했습니다..");
                throw new RuntimeException(e);
            } finally {
                redisLock.unLock(lockKey, lockValue);
                log.info(lockKey + " 키의 락을 해제합니다.");
            }
        }else{
            throw new IllegalArgumentException("현재 포인트 충전 요청 처리 중입니다.");
        }

    }

    @Transactional
    protected PointCommand chargePointTransactional(long userId, long amount){
        // 유저 포인트 충전
        PointInfo updatePointInfo = pointService.chargeUserPoint(userId,amount);
        // 내역 생성
        paymentService.createPayment(userId, amount, PaymentType.CHARGE);
        return new PointCommand(updatePointInfo.userId(), updatePointInfo.userPoint());
    }

    public PointCommand getUserPoint(long userId){
        PointInfo pointInfo = pointService.getUserPoint(userId);
        return new PointCommand(pointInfo.userId(), pointInfo.userPoint());
    }

}
