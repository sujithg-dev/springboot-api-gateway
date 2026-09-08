package com.springbootapigateway.service;

import com.springbootapigateway.ratelimit.TokenBucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService
{
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private final int capacity;
    private final double refillRatePerSecond;

    public RateLimitService(@Value("${rate-limit.capacity}") int capacity, @Value("${rate-limit.requests-per-minute}") int requestsPerMinute)
    {
        this.capacity = capacity;
        this.refillRatePerSecond = requestsPerMinute / 60.0;
    }

    public boolean allowRequest(String key)
    {
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(capacity, refillRatePerSecond));
        return bucket.allowRequest();
    }
}