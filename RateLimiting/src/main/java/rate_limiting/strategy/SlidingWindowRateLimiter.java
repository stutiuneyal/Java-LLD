package main.java.rate_limiting.strategy;

import java.util.UUID;

import main.java.rate_limiting.models.RateLimitDecision;
import main.java.rate_limiting.models.RateLimitPolicy;
import main.java.rate_limiting.redis.RedisClient;

public class SlidingWindowRateLimiter implements RateLimiterStrategy {

    private final RedisClient redisClient;

    public SlidingWindowRateLimiter(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public RateLimitDecision allowRequest(String userId, RateLimitPolicy policy) {
        int maxRequests = policy.getMaxRequests();
        int windowSizeSeconds = policy.getWindowSizeSeconds();

        long nowMillis = System.currentTimeMillis();
        long windowStartMillis = nowMillis - (windowSizeSeconds * 1000l);

        String key = "rate_limit:sliding:" + userId;

        // Remove the expired entries first
        redisClient.zRemoveRangeByScore(key, Long.MIN_VALUE, windowStartMillis - 1);

        long currentCount = redisClient.zCard(key);

        if (currentCount < maxRequests) {
            String member = nowMillis + "-" + UUID.randomUUID();
            redisClient.zAdd(key, nowMillis, member);
            long remaining = maxRequests - (currentCount + 1);

            return new RateLimitDecision(true,
                    remaining,
                    0,
                    "Allowed under sliding window");
        }

        return new RateLimitDecision(false,
                    0,
                    1,
                    "Rate limite exceeded under sliding window");

    }

}
