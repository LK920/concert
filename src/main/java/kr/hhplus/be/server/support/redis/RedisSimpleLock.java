package kr.hhplus.be.server.support.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisSimpleLock {

    private final RedisTemplate<String, Object> redisTemplate;

    /*
    * 락 획득 시도
    * @param key 락 키
    * @param value 식별자
    * @expireTimeMillis 만료시간 ms
    * @return 성공 여부
    * opsForValue()
    * setIfAbsent(key, value, expireTime)
    * */
    public boolean tryLock(String key, String value, long expireTimeMillis){
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(expireTimeMillis));
    }
    /*
    * 락 해제
    * */
    public void unLock(String key, String value){
        Object storedValue = redisTemplate.opsForValue().get(key);
        if(value.equals(storedValue)){
            log.info("저장된 키 : {}\n 저장된 값 : {}", key, storedValue);
            redisTemplate.delete(key);
        }
    }
}
