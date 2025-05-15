package kr.hhplus.be.server.domain.ranking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final ConcertRepository concertRepository;
    private final ObjectMapper objectMapper;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void addConcertRankingScore(long concertId){
        String todayKey = "concert:ranking:" + LocalDate.now().format(formatter);
        rankingRepository.addRankingScore(todayKey, 1, String.valueOf(concertId));
    }

    public void setAggregateRanking(int days, String targetKey){

        // 어제까지 N일 전까지 key 수집 ["concert:ranking:20250501", "concert:ranking:20250502"....]
        List<String> keys = generateKeys(days);
        // 1. Redis ZUNIONSTORE로 합산 (임시 key 사용 후 삭제) -> score 합산
        unionAndStoreRanking(keys, targetKey, 1l);
        // 2. 점수 기준 내림차순 정렬된 콘서트 ID 리스트 가져오기
        List<Long> concertIds = getRankedConcertIds(targetKey);
        if (concertIds.isEmpty()) {
            log.info("No concert ranking data for {}", targetKey);
            return;
        }
        // 3. 콘서트 정보 조회 & JSON 변환
        List<String> rankingJsonList = concertRankingJson(concertIds);
        // 4. Redis List 저장
        saveRankingResult(targetKey, rankingJsonList);

    }

    private List<String> generateKeys(int days){
        LocalDate today = LocalDate.now();
        List<String> keys = new ArrayList<>();

        for (int i = days; i >= 1; i--) {
            keys.add("concert:ranking:" + today.minusDays(i).format(formatter));
        }
        return keys;
    }

    private void unionAndStoreRanking(List<String> sourceKeys, String targetKey, long ttlDays){
        rankingRepository.unionAndStoreRanking(sourceKeys, targetKey, Duration.ofDays(ttlDays));
    }

    private List<Long> getRankedConcertIds(String targetKey){
        List<String> topMembersByScoreDesc = rankingRepository.getTopMembersByScoreDesc(targetKey);
        List<Long> concertIds = topMembersByScoreDesc.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return concertIds;
    }

    private List<String> concertRankingJson(List<Long> concertIds) {
        List<Concert> concerts = concertRepository.findAllByIdIn(concertIds);
        Map<Long, Concert> concertMap = concerts.stream()
                .collect(Collectors.toMap(Concert::getId, Function.identity()));

        List<String> jsonList = new ArrayList<>();
        for (Long id : concertIds) {
            Concert concert = concertMap.get(id);
            if (concert == null) continue;

            Map<String, Object> data = Map.of(
                    "concertId", concert.getId(),
                    "concertName", concert.getConcertName()
            );

            try {
                jsonList.add(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                log.error("Json 직렬화 오류: {}", concert.getId(), e);
            }
        }
        return jsonList;
    }

    private void saveRankingResult(String key, List<String> jsonList) {
        rankingRepository.saveRankingInfo(key, jsonList, Duration.ofDays(1));
    }

    public List<Ranking> concertRanking(String rankingType) {
        String redisKey = getRedisKey(rankingType);

        List<String> rankingJsonList = rankingRepository.getRankingInfo(redisKey);
        if (rankingJsonList == null || rankingJsonList.isEmpty()) {
            log.info("No ranking data found for key: {}", redisKey);
            return Collections.emptyList();
        }

        List<Ranking> result = new ArrayList<>();
        int rank = 1;
        for (String json : rankingJsonList) {
            try {
                RankingConcert data = objectMapper.readValue(json, RankingConcert.class);
                result.add(new Ranking(rank++, data.concertId(), data.concertName()));
            } catch (JsonProcessingException e) {
                log.error("JSON 역직렬화 실패: {}", json, e);
            }
        }
        return result;
    }

    private String getRedisKey(String rankingType) {
        return switch (rankingType) {
            case "daily" -> "concert:ranking:daily";
            case "weekly" -> "concert:ranking:weekly";
            case "monthly" -> "concert:ranking:monthly";
            default -> throw new IllegalArgumentException("Unknown ranking type: " + rankingType);
        };
    }
}
