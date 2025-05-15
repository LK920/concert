package kr.hhplus.be.server;


import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisTestConfig {

    private static final String redis_image = "redis:latest";
    private static final int redis_port = 6379;
    private static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(redis_image)
                .withExposedPorts(redis_port)
                .withReuse(true);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperty(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        dynamicPropertyRegistry.add("spring.redis.port", ()->REDIS_CONTAINER.getMappedPort(redis_port)
                .toString());
    }

}

