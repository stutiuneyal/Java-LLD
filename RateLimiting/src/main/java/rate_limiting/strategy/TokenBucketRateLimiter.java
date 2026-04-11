package main.java.rate_limiting.strategy;

import java.util.Map;

import main.java.rate_limiting.models.RateLimitDecision;
import main.java.rate_limiting.models.RateLimitPolicy;
import main.java.rate_limiting.redis.RedisClient;

public class TokenBucketRateLimiter implements RateLimiterStrategy {

    private final RedisClient redisClient;

    public TokenBucketRateLimiter(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    @Override
    public RateLimitDecision allowRequest(String userId, RateLimitPolicy policy) {

        int capacity = policy.getBucketCapacity();
        int refillTokens = policy.getRefillTokens();
        int refillIntervalSeconds = policy.getRefillIntervalSeconds();

        String key = "rate_limit:token_bucket:" + userId;
        long nowMillis = System.currentTimeMillis();

        Map<String, String> bucketData = redisClient.hGetAll(key);

        double availableTokens;
        long lastRefillTimeInMillis;

        if (bucketData.isEmpty()) {
            availableTokens = capacity;
            lastRefillTimeInMillis = nowMillis;
        } else {
            availableTokens = Double.parseDouble(bucketData.getOrDefault("tokens", String.valueOf(capacity)));
            lastRefillTimeInMillis = Long
                    .parseLong(bucketData.getOrDefault("lastRefillTimeInMillis", String.valueOf(nowMillis)));
        }

        double tokensToAdd = ((double) (nowMillis - lastRefillTimeInMillis)) / (refillIntervalSeconds * 1000l)
                * refillTokens;
        availableTokens = Math.min(capacity, availableTokens + tokensToAdd);

        if (availableTokens >= 1.0) {
            availableTokens -= 1.0;
            redisClient.hSet(key, "tokens", String.valueOf(availableTokens));
            redisClient.hSet(key, "lastRefillTimeInMillis", String.valueOf(nowMillis));

            return new RateLimitDecision(true,
                    (long) Math.floor(availableTokens),
                    0,
                    "Allowed under Token Bucket");
        }

        double missingTokens = 1.0 - availableTokens;
        double tokensPerMillis = (double) refillTokens / (refillIntervalSeconds *1000l);
        long retryAfterMillis = (long) Math.ceil(missingTokens / tokensPerMillis);

        /*
        availableTokens = 0.3
        missingTokens = 0.7

        tokenPerMillis = 0.0005

        retry = 0.7/0.0005 = 1400ms -> 1.4seconds -> 2
        */

        redisClient.hSet(key, "tokens", String.valueOf(availableTokens));
        redisClient.hSet(key, "lastRefillTimeInMillis", String.valueOf(nowMillis));

        return new RateLimitDecision(false,
                    0,
                    Math.max(1, retryAfterMillis/1000),
                    "Rate limit exceeded under Token Bucket");
    }

}
