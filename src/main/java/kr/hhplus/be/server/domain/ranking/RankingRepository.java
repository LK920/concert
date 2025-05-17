package kr.hhplus.be.server.domain.ranking;

import java.time.Duration;
import java.util.List;

public interface RankingRepository {
    void addRankingScore(String key, double score, String member);
    List<String> getTopMembersByScoreDesc(String key);
    void saveRankingInfo(String key, List<String> rankingList, Duration ttl);
    void unionAndStoreRanking(List<String> sourceKeys, String targetKey, Duration ttl);
    List<String> getRankingInfo(String key);
}