package kr.hhplus.be.server.support.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
/*
* 분산락에 사용할 어노테이션 생성
* 분산락 타겟 : method
* 어노테이션 생명주기 : runtime
* */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /*
    * 락 이름
    * */
    String key();

    /*
    * 락의 시간 단위 기본 sec
    * */
    TimeUnit timeunit() default TimeUnit.SECONDS;

    /*
    * 락 획득 이후 점유 시간 기본 3초
    * */
    long leaseTime() default 3l;

    /*
    * 락 획득을 위해 대기하는 시간 기본 5초
    * */
    long waitTime() default 5l;
}
