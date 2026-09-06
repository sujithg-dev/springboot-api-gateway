package com.springbootapigateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest
{
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp()
    {
        rateLimitService = new RateLimitService(3, 3);
    }

    @Test
    void allowRequest_shouldAllowRequest_whenTokensAreAvailable()
    {
        boolean result = rateLimitService.allowRequest("user1");
        assertTrue(result);
    }

    @Test
    void allowRequest_shouldAllowRequestsUpToCapacity()
    {
        boolean request1 = rateLimitService.allowRequest("user1");
        boolean request2 = rateLimitService.allowRequest("user1");
        boolean request3 = rateLimitService.allowRequest("user1");

        assertTrue(request1);
        assertTrue(request2);
        assertTrue(request3);
    }

    @Test
    void allowRequest_shouldRejectRequest_whenBucketIsEmpty()
    {
        rateLimitService.allowRequest("user1");
        rateLimitService.allowRequest("user1");
        rateLimitService.allowRequest("user1");

        boolean result = rateLimitService.allowRequest("user1");

        assertFalse(result);
    }

    @Test
    void allowRequest_shouldMaintainSeparateBucketsForDifferentKeys()
    {
        rateLimitService.allowRequest("user1");
        rateLimitService.allowRequest("user1");
        rateLimitService.allowRequest("user1");

        boolean result = rateLimitService.allowRequest("user2");

        assertTrue(result);
    }

    @Test
    void allowRequest_shouldRejectOnlyExceededKey()
    {
        rateLimitService.allowRequest("user1");
        rateLimitService.allowRequest("user1");
        rateLimitService.allowRequest("user1");

        boolean user1Result = rateLimitService.allowRequest("user1");
        boolean user2Result = rateLimitService.allowRequest("user2");

        assertFalse(user1Result);
        assertTrue(user2Result);
    }
}
