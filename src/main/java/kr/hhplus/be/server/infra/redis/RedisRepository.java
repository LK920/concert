package kr.hhplus.be.server.infra.redis;

import kr.hhplus.be.server.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class RedisRepository implements RankingRepository {

    private final RedissonClient redissonClient;
    @Override
    public void addRankingScore(String key, double score, String member){
        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(key, StringCodec.INSTANCE);
        zSet.addScore(member, score);
//        ttl 없을 시 추가
        if(zSet.remainTimeToLive() == -1){
            zSet.expire(Duration.ofDays(30));
        }
    }

    @Override
    public List<String> getTopMembersByScoreDesc(String key){
        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(key, StringCodec.INSTANCE);
        Collection<String> range = zSet.valueRangeReversed(0, -1);
        return new ArrayList<>(range);
    }

    @Override
    public void saveRankingInfo(String key, List<String> rankingList, Duration ttl) {
        RList<String> redisList = redissonClient.getList(key, StringCodec.INSTANCE);
        redisList.clear();
        redisList.addAll(rankingList);
        redisList.expire(ttl);
    }

    @Override
    public void unionAndStoreRanking(List<String> sourceKeys, String targetKey, Duration ttl) {
        // 소스로 사용하는 keys 가 없으면 취소
        if (sourceKeys == null || sourceKeys.isEmpty()) return;

        String[] keysArray = sourceKeys.toArray(new String[0]);
        RScoredSortedSet<String> zSet = redissonClient.getScoredSortedSet(targetKey, StringCodec.INSTANCE);
        // Redis에서 기존 key 삭제 (중복 방지)
        zSet.clear();
        // ZUNIONSTORE: 점수 합산
        zSet.union(keysArray);
        zSet.expire(ttl);
    }

    @Override
    public List<String> getRankingInfo(String key) {
        RList<String> rankingList = redissonClient.getList(key);
        return rankingList.readAll();
    }

}
