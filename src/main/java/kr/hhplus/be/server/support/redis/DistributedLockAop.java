package kr.hhplus.be.server.support.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class DistributedLockAop {

    private static final String REDISSON_LOCK_PREFIX = "lock:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(distributedLock)")
    public Object around(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable{

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        EvaluationContext context = new MethodBasedEvaluationContext(null, method, joinPoint.getArgs(), new DefaultParameterNameDiscoverer());
        ExpressionParser parser = new SpelExpressionParser();

        String parsedKey = parser.parseExpression(distributedLock.key()).getValue(context, String.class);
        String key = REDISSON_LOCK_PREFIX + parsedKey;

        log.info("lock on [method:{}] [key:{}]", method, key);

        RLock rLock = redissonClient.getLock(key); // 사용할 락 지정

        boolean acquired = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.SECONDS);

        if(!acquired){
            throw new IllegalStateException("Lock 획득 실패 : " + key);
        }

        try {
            log.info("락 획득 : {}", key);
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e){
            log.error("Lock Interrupted Error : {}", e.getMessage());
            throw e;
        }finally {
            rLock.unlock();
            log.info("락 해제 : {}", key);
        }
    }
}
