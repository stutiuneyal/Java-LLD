package main.java.rate_limiting.service;

import main.java.rate_limiting.factory.RateLimitPolicyProvider;
import main.java.rate_limiting.factory.RateLimiterStrategyFactory;
import main.java.rate_limiting.models.RateLimitDecision;
import main.java.rate_limiting.models.RateLimitPolicy;
import main.java.rate_limiting.strategy.RateLimiterStrategy;

public class RateLimiterService {

    private final RateLimitPolicyProvider policyProvider;
    private final RateLimiterStrategyFactory strategyFactory;

    public RateLimiterService(RateLimitPolicyProvider policyProvider,
            RateLimiterStrategyFactory strategyFactory) {
        this.policyProvider = policyProvider;
        this.strategyFactory = strategyFactory;
    }

    public boolean allowRequests(String userId) {
        return allowRequestWithDetails(userId).isAllowed();
    }

    public RateLimitDecision allowRequestWithDetails(String userId) {
        RateLimitPolicy policy = policyProvider.getPolicy(userId);
        RateLimiterStrategy strategy = strategyFactory.getStrategy(policy.getAlgorithmType());

        return strategy.allowRequest(userId, policy);
    }

}
