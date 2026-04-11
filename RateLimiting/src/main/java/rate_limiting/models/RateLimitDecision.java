package main.java.rate_limiting.models;

public class RateLimitDecision {

    private final boolean allowed;
    private final long remaining;
    private final long retryAfterSeconds;
    private final String message;

    public RateLimitDecision(boolean allowed, long remaining, long retryAfterSeconds, String message) {
        this.allowed = allowed;
        this.remaining = remaining;
        this.retryAfterSeconds = retryAfterSeconds;
        this.message = message;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getRemaining() {
        return remaining;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RateLimitDecision{" +
                "allowed=" + allowed +
                ", remaining=" + remaining +
                ", retryAfterSeconds=" + retryAfterSeconds +
                ", message='" + message + '\'' +
                '}';
    }

}
