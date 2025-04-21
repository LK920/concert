package kr.hhplus.be.server.domain.concertDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
/*
* contains(...)	순서 상관없이 포함되기만 하면 OK (전부 포함 안 돼도 통과할 수도 있음)
* containsExactly(...)	순서 포함해서 정확히 일치해야 함
* containsExactlyInAnyOrder(...)	순서 무시, 내용이 정확히 일치해야 함
* */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
public class ConcertDateIntegrationTest {

    @Autowired
    private ConcertDateService concertDateService;
    @Autowired
    private ConcertDateRepository concertDateRepository;

    @BeforeEach
    void setUp(){
        concertDateRepository.deleteAll();

        LocalDateTime datetime = LocalDateTime.of(2025,05,01,10,11,00);
        for(int i = 0; i< 3; i++){
            ConcertDate concertDate = ConcertDate.create(1l, datetime.plusDays(i));
            concertDateRepository.save(concertDate);
        }
    }

    @Test
    @DisplayName("콘서트_날짜_조회_실패")
    void getConcertDates_fail(){
        long concertId = 99L;
        List<ConcertDateInfo> list = concertDateService.getConcertAvailableDates(concertId);
        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("콘서트_날짜_조회_성공")
    void getConcertDates_succes(){
        long concertId = 1l;

        List<ConcertDateInfo> list = concertDateService.getConcertAvailableDates(concertId);

        assertThat(list).hasSize(3);
        assertThat(list).extracting(ConcertDateInfo::concertDate)
                .containsExactlyInAnyOrder(
                        LocalDateTime.of(2025, 5, 1, 10, 11),
                        LocalDateTime.of(2025, 5, 2, 10, 11),
                        LocalDateTime.of(2025, 5, 3, 10, 11)
                );
    }
}
