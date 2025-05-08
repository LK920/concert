package kr.hhplus.be.server.domain.concert;


import kr.hhplus.be.server.support.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
/*
* @TestContainer => @Container 쓸 때 자동으로 수명 주기 관리해주는 용도
* 테스트컨테이너 설정에서 이미 시작과 끝을 제어하고 있다면 사용안해도된다.
* */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Slf4j
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisService.flushAll();
        concertRepository.deleteAll();
    }

    @Test
    @DisplayName("콘서트_조회_성공")
    void getConcerts_success() {

        IntStream.rangeClosed(1, 20).forEach(i -> {
            Concert concert = Concert.create("Concert " + i, 100 + i);
            concertRepository.save(concert);
        });

        List<ConcertInfo> result = concertService.getConcerts();
        log.info(result.get(0).toString());
        assertThat(result).hasSize(20);
    }

    @Test
    @DisplayName("콘서트_조회_빈값")
    void getConcerts_empty() {
        List<ConcertInfo> result = concertService.getConcerts();

        assertThat(result).isEmpty();
    }

}
