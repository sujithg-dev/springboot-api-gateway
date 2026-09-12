package com.springbootapigateway.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class GatewayMetricsService
{
    private final Counter totalRequests;
    private final Counter allowedRequests;
    private final Counter rejectedRequests;
    private final Counter authenticationFailures;

    public GatewayMetricsService(MeterRegistry meterRegistry)
    {
        totalRequests = Counter.builder("gateway.rate_limit.total").register(meterRegistry);
        allowedRequests = Counter.builder("gateway.rate_limit.allowed").register(meterRegistry);
        rejectedRequests = Counter.builder("gateway.rate_limit.rejected").register(meterRegistry);
        authenticationFailures = Counter.builder("gateway.authentication.failures").register(meterRegistry);
    }

    public void countTotalRequests()
    {
        totalRequests.increment();
    }

    public void countAllowedRequests()
    {
        allowedRequests.increment();
    }

    public void countRejectedRequests()
    {
        rejectedRequests.increment();
    }

    public void countAuthenticationFailures()
    {
        authenticationFailures.increment();
    }
}