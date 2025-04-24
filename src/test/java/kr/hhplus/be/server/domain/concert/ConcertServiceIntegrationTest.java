package kr.hhplus.be.server.domain.concert;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
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

        assertThat(result).hasSize(5);
        assertThat(result.get(0).concertName()).contains("Concert");
    }

    @Test
    @DisplayName("콘서트_조회_빈값")
    void getConcerts_empty() {
        List<ConcertInfo> result = concertService.getConcerts();

        assertThat(result).isEmpty();
    }

}
