package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentType;
import kr.hhplus.be.server.domain.point.PointInfo;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.support.redis.DistributedLock;
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

    /*
    * 분산락 aop 적용
    * redisson을 사용하여 pub/sub
    * waitTime => 0 으로 하여 심플락처럼 구현
    * */
    @DistributedLock(key = "'point:' + #userId", leaseTime = 3l, waitTime = 0)
    @Transactional
    public PointCommand chargePoint(long userId, long amount){
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
