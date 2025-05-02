package kr.hhplus.be.server;

import kr.hhplus.be.server.support.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

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

}
