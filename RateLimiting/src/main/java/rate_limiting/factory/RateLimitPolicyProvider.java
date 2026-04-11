package main.java.rate_limiting.factory;

import main.java.rate_limiting.models.RateLimitPolicy;

public interface RateLimitPolicyProvider {
    RateLimitPolicy getPolicy(String userId);
}
