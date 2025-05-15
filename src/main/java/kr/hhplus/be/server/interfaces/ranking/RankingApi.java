package kr.hhplus.be.server.interfaces.ranking;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "랭킹 조회 api")
@RequestMapping("/ranking")
public interface RankingApi {

    @GetMapping("/{rankingType}")
    default ResponseEntity<List<ResponseRankingDTO>> concertRanking(@PathVariable("rankingType") String rankingType){
        List<ResponseRankingDTO> mockResponse = List.of(
                new ResponseRankingDTO(1, 1, "Concert A"),
                new ResponseRankingDTO(2, 2, "Concert B")
                );
        return ResponseEntity.ok(mockResponse);
    }

}
