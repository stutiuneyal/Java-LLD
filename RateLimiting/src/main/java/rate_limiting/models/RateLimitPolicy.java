package main.java.rate_limiting.models;

import main.java.rate_limiting.enums.RateLimitingAlgorithmType;

public class RateLimitPolicy {

    private final String userId;
    private final RateLimitingAlgorithmType algorithmType;

    // Fixed & Sliding Window
    private final int maxRequests;
    private final int windowSizeSeconds;

    // Token Bucket
    private final int bucketCapacity;
    private final int refillTokens;
    private final int refillIntervalSeconds;

    public RateLimitPolicy(Builder builder) {
        this.userId = builder.userId;
        this.algorithmType = builder.algorithmType;
        this.maxRequests = builder.maxRequests;
        this.windowSizeSeconds = builder.windowSizeSeconds;
        this.bucketCapacity = builder.bucketCapacity;
        this.refillTokens = builder.refillTokens;
        this.refillIntervalSeconds = builder.refillIntervalSeconds;
    }

    public String getUserId() {
        return userId;
    }

    public RateLimitingAlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public int getWindowSizeSeconds() {
        return windowSizeSeconds;
    }

    public int getBucketCapacity() {
        return bucketCapacity;
    }

    public int getRefillTokens() {
        return refillTokens;
    }

    public int getRefillIntervalSeconds() {
        return refillIntervalSeconds;
    }

    public static class Builder {
        private String userId;
        private RateLimitingAlgorithmType algorithmType;
        private int maxRequests;
        private int windowSizeSeconds;
        private int bucketCapacity;
        private int refillTokens;
        private int refillIntervalSeconds;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder algorithmType(RateLimitingAlgorithmType algorithmType) {
            this.algorithmType = algorithmType;
            return this;
        }

        public Builder maxRequests(int maxRequests) {
            this.maxRequests = maxRequests;
            return this;
        }

        public Builder windowSizeSeconds(int windowSizeSeconds) {
            this.windowSizeSeconds = windowSizeSeconds;
            return this;
        }

        public Builder bucketCapacity(int bucketCapacity) {
            this.bucketCapacity = bucketCapacity;
            return this;
        }

        public Builder refillTokens(int refillTokens) {
            this.refillTokens = refillTokens;
            return this;
        }

        public Builder refillIntervalSeconds(int refillIntervalSeconds) {
            this.refillIntervalSeconds = refillIntervalSeconds;
            return this;
        }

        public RateLimitPolicy build() {
            return new RateLimitPolicy(this);
        }

    }

}
