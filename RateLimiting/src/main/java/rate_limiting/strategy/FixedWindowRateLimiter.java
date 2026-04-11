package main.java.rate_limiting.strategy;

import main.java.rate_limiting.models.RateLimitDecision;
import main.java.rate_limiting.models.RateLimitPolicy;
import main.java.rate_limiting.redis.RedisClient;

public class FixedWindowRateLimiter implements RateLimiterStrategy {

    private final RedisClient redisClient;

    public FixedWindowRateLimiter(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public RateLimitDecision allowRequest(String userId, RateLimitPolicy policy) {
        int maxRequests = policy.getMaxRequests();
        int windowSizeSeconds = policy.getWindowSizeSeconds();

        long currentEpochSeconds = System.currentTimeMillis() / 1000l;

        long windowStart = (currentEpochSeconds / windowSizeSeconds) * windowSizeSeconds;

        /*
         * currentEpochSeconds = 123
         * windowSizeSeconds = 10
         * 
         * currentEpochSeconds/windowSizeSeconds = 123/10 = 12
         * 12 * windowSizeSeconds = 12 * 10 = 120
         * 
         * current request: [120, 129]
         */

        String key = "rate_limit:fixed:" + userId + ":" + windowStart;

        long count = redisClient.increment(key);

        if (count == 1) {
            redisClient.expire(key, windowSizeSeconds + 1);
        }

        if (count <= maxRequests) {
            return new RateLimitDecision(
                    true,
                    maxRequests - count,
                    0,
                    "Allowed under fixed Window");
        }

        long retryAfter = (windowStart + windowSizeSeconds) - currentEpochSeconds;

        return new RateLimitDecision(
                    false,
                    0,
                    retryAfter,
                    "Rate Limit exceeded under fixed Window");
    }

}
