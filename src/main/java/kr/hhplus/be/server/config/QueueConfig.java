package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.queue.redis.WaitingQueueStorage;
import kr.hhplus.be.server.infra.queue.RedisWaitingQueueStorage;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Bean
    public WaitingQueueStorage waitingQueueStorage(RedissonClient redissonClient) {
        return new RedisWaitingQueueStorage(redissonClient);
    }
}
