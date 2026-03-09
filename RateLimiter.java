import java.util.*;

class TokenBucket {
    private int tokens;
    private final int maxTokens;
    private final int refillRate;
    private long lastRefillTime;

    public TokenBucket(int maxTokens, int refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = (now - lastRefillTime) / 1000;
        int refillTokens = (int) (elapsed * refillRate);

        if (refillTokens > 0) {
            tokens = Math.min(maxTokens, tokens + refillTokens);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    public int getRemainingTokens() {
        refill();
        return tokens;
    }
}

public class RateLimiter {

    private Map<String, TokenBucket> clients = new HashMap<>();
    private final int MAX_REQUESTS = 1000;
    private final int REFILL_RATE = 1000 / 3600;

    public boolean checkRateLimit(String clientId) {

        TokenBucket bucket = clients.computeIfAbsent(
                clientId,
                k -> new TokenBucket(MAX_REQUESTS, REFILL_RATE)
        );

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
        } else {
            System.out.println("Denied (Rate limit exceeded)");
        }

        return allowed;
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("No usage yet.");
            return;
        }

        int remaining = bucket.getRemainingTokens();
        int used = MAX_REQUESTS - remaining;

        System.out.println("{used: " + used + ", limit: " + MAX_REQUESTS + "}");
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");

        limiter.getRateLimitStatus("abc123");
    }
}