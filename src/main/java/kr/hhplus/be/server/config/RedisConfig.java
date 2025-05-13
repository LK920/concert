package kr.hhplus.be.server.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    /*
    * Redis 연결을 위한 connection 생성
    * */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(host, port);
    }
    /*
    * RedisTemplate<key,value>를 사용하여 직렬화하여 사용
    * 직렬화시 사용자가 알아보기 어렵고, 호환성 문제가 생길 수 있기에 커스터마이징하여 사용
    * 만약 Object 타입인 Value에 복잡한 객체(예를 들면 Java의 User 클래스 인스턴스)를 저장할 일이 있다면,
    * StringRedisSerializer 대신 GenericJackson2JsonRedisSerializer 같은 걸 써야한다.
    * */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
//        템플릿 생성
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redis 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL_AND_ENUMS,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(om);
//      key-value 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
//        hash 직렬화
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
//        직렬화 기본값으로 설정(string, hash 자료구조 외의 사용시 string 직렬화하도록 하는 설정)
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
