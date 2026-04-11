package main.java.rate_limiting.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.java.rate_limiting.models.RateLimitPolicy;

public class RateLimitPolicyProviderFactory implements RateLimitPolicyProvider {

    private final Map<String, RateLimitPolicy> policyMap = new ConcurrentHashMap<>();

    public void addPolicy(RateLimitPolicy policy) {
        policyMap.put(policy.getUserId(), policy);
    }

    @Override
    public RateLimitPolicy getPolicy(String userId) {
        RateLimitPolicy policy = policyMap.get(userId);
        if (policy == null) {
            throw new IllegalArgumentException("No rate limit policy found for user: " + userId);
        }
        return policy;
    }

}
