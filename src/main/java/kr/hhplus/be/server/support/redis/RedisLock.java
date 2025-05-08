package kr.hhplus.be.server.support.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisLock {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.expire-time-ms}")
    private long expireTimeMillis;

    /*
    * 락 획득 시도
    * @param key 락 키
    * @param value 식별자
    * @expireTimeMillis 만료시간 ms
    * @return 성공 여부
    * opsForValue()
    * setIfAbsent(key, value, expireTime)
    * */
    public boolean tryLock(String key, String value){
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(expireTimeMillis));
    }

    public boolean tryLock(String key, String value, long expireMillis){
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(expireMillis));
    }

    /*
    * 락 해제
    * lua 스크립트로 get() 과 delete() 사이에 race condition 가능성 없앰
    * 일반적으로 unlock시 get()과 delete()로 처리하면 중간에 락획득이 가능해짐
    * */
    public void unLock(String key, String value){
//        Object storedValue = redisTemplate.opsForValue().get(key);
//        if(value.equals(storedValue)){
//            log.info("저장된 키 : {}\n 저장된 값 : {}", key, storedValue);
//            redisTemplate.delete(key);
//        }
        String luaScript =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del',KEYS[1]) " +
                "else " +
                    "return 0 " +
                "end";
        redisTemplate.execute(
                new DefaultRedisScript<>(luaScript, Long.class),
                Collections.singletonList(key),
                value
        );
    }
}
