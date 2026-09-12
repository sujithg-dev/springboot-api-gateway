package com.springbootapigateway.filter;

import com.springbootapigateway.service.GatewayMetricsService;
import com.springbootapigateway.service.RateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter
{
    private final RateLimitService rateLimitService;
    private final GatewayMetricsService metricsService;

    public RateLimitFilter(GatewayMetricsService metricsService, RateLimitService rateLimitService)
    {
        this.metricsService = metricsService;
        this.rateLimitService = rateLimitService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username;

        if (request.getRequestURI().startsWith("/actuator/"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        metricsService.countTotalRequests();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal()))
        {
            username = authentication.getName();
        }
        else
        {
            username = request.getRemoteAddr();
        }

        if (!rateLimitService.allowRequest(username))
        {
            metricsService.countRejectedRequests();

            request.setAttribute("rateLimitExceeded", true);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                        "Status": "429 - Too Many Requests",
                        "message": "Rate limit exceeded"
                    }
                    """);

            return;
        }

        metricsService.countAllowedRequests();

        filterChain.doFilter(request, response);
    }
}