package com.springbootapigateway.ratelimit;

public class TokenBucket
{
    private final int capacity;
    private final double refillRatePerSecond;

    private double tokens;
    private long lastRefillTime;

    public TokenBucket(int capacity, double refillRatePerSecond)
    {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;

        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public synchronized boolean allowRequest()
    {
        refill();

        if (tokens >= 1)
        {
            tokens--;
            return true;
        }

        return false;
    }

    private void refill()
    {
        long currentTime = System.nanoTime();
        long elapsedNanos = currentTime - lastRefillTime;
        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
        double tokensToAdd = elapsedSeconds * refillRatePerSecond;

        tokens = Math.min(capacity, tokens + tokensToAdd);

        lastRefillTime = currentTime;
    }
}