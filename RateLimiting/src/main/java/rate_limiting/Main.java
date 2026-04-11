package main.java.rate_limiting;

import main.java.rate_limiting.enums.RateLimitingAlgorithmType;
import main.java.rate_limiting.factory.RateLimitPolicyProviderFactory;
import main.java.rate_limiting.factory.RateLimiterStrategyFactory;
import main.java.rate_limiting.models.RateLimitPolicy;
import main.java.rate_limiting.redis.RedisClient;
import main.java.rate_limiting.redis.RedisClientImpl;
import main.java.rate_limiting.service.RateLimiterService;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        RedisClient redisClient = new RedisClientImpl();

        RateLimitPolicyProviderFactory policyProvider = new RateLimitPolicyProviderFactory();

        policyProvider.addPolicy(new RateLimitPolicy.Builder()
                .userId("user-fixed")
                .algorithmType(RateLimitingAlgorithmType.FIXED_WINDOW)
                .maxRequests(3)
                .windowSizeSeconds(10)
                .build());

        policyProvider.addPolicy(new RateLimitPolicy.Builder()
                .userId("user-sliding")
                .algorithmType(RateLimitingAlgorithmType.SLIDING_WINDOW)
                .maxRequests(3)
                .windowSizeSeconds(10)
                .build());

        policyProvider.addPolicy(new RateLimitPolicy.Builder()
                .userId("user-bucket")
                .algorithmType(RateLimitingAlgorithmType.TOKEN_BUCKET)
                .bucketCapacity(5)
                .refillTokens(1)
                .refillIntervalSeconds(2)
                .build());

        RateLimiterStrategyFactory strategyFactory = new RateLimiterStrategyFactory(redisClient);
        RateLimiterService rateLimiterService = new RateLimiterService(policyProvider, strategyFactory);

        System.out.println("==== FIXED WINDOW ====");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + rateLimiterService.allowRequestWithDetails("user-fixed"));
        }

        System.out.println("\n==== SLIDING WINDOW ====");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + rateLimiterService.allowRequestWithDetails("user-sliding"));
        }

        System.out.println("\n==== TOKEN BUCKET ====");
        for (int i = 1; i <= 7; i++) {
            System.out.println("Request " + i + ": " + rateLimiterService.allowRequestWithDetails("user-bucket"));
        }

        Thread.sleep(3000);

        System.out.println("\n==== TOKEN BUCKET AFTER REFILL ====");
        for (int i = 1; i <= 3; i++) {
            System.out.println("Request " + i + ": " + rateLimiterService.allowRequestWithDetails("user-bucket"));
        }
    }
}
