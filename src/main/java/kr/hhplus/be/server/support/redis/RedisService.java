package kr.hhplus.be.server.support.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key){
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    public void del(String key){
        redisTemplate.delete(key);
    }

    public void flushAll(){
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    public void zAdd(String key, long score, String member){
        /*
        * redisson RSortedSet ->redis 자료형 list, set
        * redisson RScoreSortedSet -> redis 자료현 zSet
        * codec 설정을 해줘야지 직렬화 없이 string으로 저장
        * */
        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(key, StringCodec.INSTANCE);

        zSet.add(score, member); // member의 score를 update(없으면 생성)
    }

    public double zIncrBy(String key, double score, String member ){
        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(key, StringCodec.INSTANCE);
        return zSet.addScore(member,score); //member의 score를 추가
    }

     public Collection<ScoredEntry<String>> zRangeByScore(String key){
        RScoredSortedSet<String> zSets = redissonClient.getScoredSortedSet(key, StringCodec.INSTANCE);
        return zSets.entryRange(0,-1);
    }
}
