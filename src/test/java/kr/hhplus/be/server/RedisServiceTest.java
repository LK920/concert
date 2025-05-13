package kr.hhplus.be.server;

import kr.hhplus.be.server.support.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    void redisRegisterStrings(){
        String key = "bowtie";
        String value = "red-color";

        redisService.save(key, value);

        String result = redisService.get(key);
        assertThat(result).isEqualTo(value);
    }

    @Test
    void redisDeleteString(){
        String key = "bowtie";
        String value = "blue-color";
        redisService.save(key, value);

        redisService.del(key);

        Object result = redisService.get(key);

        assertThat(result).isNull();
    }

    @Test
    void redissonZAdd(){
        LocalDate now = LocalDate.now();
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd");
        String key = "concert:" + now.format(ofPattern);
        int count = 10;
        Random random = new Random();
        for(int i = 1; i < count; i ++){
            String member = "item" + i;
            int number = random.nextInt(10) + 1;
            redisService.zAdd(key, number, member);
        }

        Collection<ScoredEntry<String>> zSets = redisService.zRangeByScore(key);
        zSets.stream().forEach(zset -> log.info("member : {}, score : {}", zset.getValue(), zset.getScore()));
    }

    @Test
    void zIncrBy(){
        String key = "concert:20250513";
        String member = "item4";
        double score = 12;

        double updatedScore = redisService.zIncrBy(key,score, member);

        RScoredSortedSet<String> zSets = redissonClient.getScoredSortedSet(key, StringCodec.INSTANCE);

        assertThat(zSets.getScore(member)).isEqualTo(updatedScore);
    }

}
