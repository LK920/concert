package kr.hhplus.be.server.domain.queue.redis;

import java.util.List;

public interface WaitingQueueStorage {

    void enqueue(String token, long userId);

    boolean isActive(String token);

    int getActiveCount();

    int getWaitingNumber(String token);

    long getOldestActiveRemainingMillis();

    void activate(String token);

    void expireInactiveTokens();

    List<String> activateFromWaiting(int count);

    long getUserId(String token);

    void removeFromActive(String token);
}
