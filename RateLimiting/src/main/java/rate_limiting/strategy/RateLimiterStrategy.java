package main.java.rate_limiting.strategy;

import main.java.rate_limiting.models.RateLimitDecision;
import main.java.rate_limiting.models.RateLimitPolicy;

public interface RateLimiterStrategy {
    RateLimitDecision allowRequest(String userId, RateLimitPolicy policy);
}
