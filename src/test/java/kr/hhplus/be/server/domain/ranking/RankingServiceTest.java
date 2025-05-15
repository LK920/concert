package kr.hhplus.be.server.domain.ranking;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RankingServiceTest {

    @InjectMocks
    private RankingService rankingService;

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testSetAggregateRanking() throws Exception {
        // given
        String targetKey = "concert:ranking:weekly";
        List<String> redisKeys = List.of("concert:ranking:20250506", "concert:ranking:20250507");
        List<String> topMembers = List.of("1", "2");
        Concert concert1 = Concert.create("Concert A", 50L);
        Concert concert2 = Concert.create("Concert B", 60L);

        ReflectionTestUtils.setField(concert1, "id", 1L);
        ReflectionTestUtils.setField(concert2, "id", 2L);

        List<Concert> concerts = List.of(concert1, concert2);

        when(rankingRepository.getTopMembersByScoreDesc(targetKey)).thenReturn(topMembers);
        when(concertRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(concerts);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"concertId\":1,\"concertName\":\"Concert A\"}");

        // when
        rankingService.setAggregateRanking(2, targetKey);

        // then
        verify(rankingRepository).unionAndStoreRanking(anyList(), eq(targetKey), any());
        verify(rankingRepository).saveRankingInfo(eq(targetKey), anyList(), any());
    }

    @Test
    void testConcertRanking() throws Exception {
        // given
        String redisKey = "concert:ranking:weekly";
        String json1 = "{\"concertId\":1,\"concertName\":\"Concert A\"}";
        String json2 = "{\"concertId\":2,\"concertName\":\"Concert B\"}";

        when(rankingRepository.getRankingInfo(redisKey)).thenReturn(List.of(json1, json2));
        when(objectMapper.readValue(json1, RankingConcert.class))
                .thenReturn(new RankingConcert(1L, "Concert A"));
        when(objectMapper.readValue(json2, RankingConcert.class))
                .thenReturn(new RankingConcert(2L, "Concert B"));

        // when
        List<Ranking> result = rankingService.concertRanking("weekly");

        // then
        assertEquals(2, result.size());
        assertEquals("Concert A", result.get(0).concertName());
        assertEquals(1L, result.get(0).concertId());
        assertEquals(1, result.get(0).rank());
    }

    @Test
    void testConcertRanking_withInvalidRankingType() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            rankingService.concertRanking("yearly");
        });
    }
}
