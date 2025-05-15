package kr.hhplus.be.server.interfaces.ranking;

import kr.hhplus.be.server.domain.ranking.Ranking;
import kr.hhplus.be.server.domain.ranking.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = RankingController.class)
class RankingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RankingService rankingService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

    }

    @DisplayName("콘서트_랭킹_일별_조회")
    @Test
    void concertRanking() throws Exception {
        // given
        List<Ranking> mockRankings = List.of(
                new Ranking(1, 1001L, "콘서트 A"),
                new Ranking(2, 1002L, "콘서트 B")
        );
        when(rankingService.concertRanking("daily")).thenReturn(mockRankings);

        // when & then
        mvc.perform(get("/ranking/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].concertId").value(1001))
                .andExpect(jsonPath("$[0].concertName").value("콘서트 A"))
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[1].concertId").value(1002))
                .andExpect(jsonPath("$[1].concertName").value("콘서트 B"));
        verify(rankingService, times(1)).concertRanking("daily");
    }
}