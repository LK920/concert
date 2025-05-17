package kr.hhplus.be.server.interfaces.ranking;

import kr.hhplus.be.server.domain.ranking.Ranking;
import kr.hhplus.be.server.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController implements RankingApi {

    private final RankingService rankingService;

    @GetMapping("/{rankingType}")
    @Override
    public ResponseEntity<List<ResponseRankingDTO>> concertRanking(@PathVariable("rankingType") String rankingType){
        List<Ranking> rankingList = rankingService.concertRanking(rankingType.toLowerCase());
        List<ResponseRankingDTO> result = rankingList.stream().map(
                ranking -> ResponseRankingDTO.from(ranking)).toList();
        return ResponseEntity.ok(result);
    }

}
