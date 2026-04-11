package main.java.rate_limiting.factory;

import java.util.EnumMap;
import java.util.Map;

import main.java.rate_limiting.enums.RateLimitingAlgorithmType;
import main.java.rate_limiting.redis.RedisClient;
import main.java.rate_limiting.strategy.FixedWindowRateLimiter;
import main.java.rate_limiting.strategy.RateLimiterStrategy;
import main.java.rate_limiting.strategy.SlidingWindowRateLimiter;
import main.java.rate_limiting.strategy.TokenBucketRateLimiter;

public class RateLimiterStrategyFactory {

    private final Map<RateLimitingAlgorithmType, RateLimiterStrategy> strategyMap = new EnumMap<>(
            RateLimitingAlgorithmType.class);

    public RateLimiterStrategyFactory(RedisClient redisClient) {
        strategyMap.put(RateLimitingAlgorithmType.FIXED_WINDOW, new FixedWindowRateLimiter(redisClient));
        strategyMap.put(RateLimitingAlgorithmType.SLIDING_WINDOW, new SlidingWindowRateLimiter(redisClient));
        strategyMap.put(RateLimitingAlgorithmType.TOKEN_BUCKET, new TokenBucketRateLimiter(redisClient));
    }

    public RateLimiterStrategy getStrategy(RateLimitingAlgorithmType algorithmType) {
        RateLimiterStrategy strategy = strategyMap.get(algorithmType);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported rate limter algorithm: " + algorithmType);
        }
        return strategy;
    }
}
