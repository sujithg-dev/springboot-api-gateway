package com.springbootapigateway.filter;

import com.springbootapigateway.model.Log;
import com.springbootapigateway.service.LogService;
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
import java.time.LocalDateTime;

@Component
public class LogFilter extends OncePerRequestFilter
{
    private final LogService logService;

    public LogFilter(LogService logService)
    {
        this.logService = logService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            filterChain.doFilter(request, response);
        }
        finally
        {
            Log log = new Log();
            log.setUsername(getUsername());
            log.setIpAddress(request.getRemoteAddr());
            log.setMethod(request.getMethod());
            log.setEndpoint(request.getRequestURI());
            log.setResponseCode(response.getStatus());
            log.setResponseStatus(getResponseStatus(response.getStatus()));
            log.setRateLimitExceeded(Boolean.TRUE.equals(request.getAttribute("rateLimitExceeded")));
            log.setTimestamp(LocalDateTime.now());
            logService.addLog(log);
        }
    }

    private String getUsername()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||  "anonymousUser".equals(authentication.getPrincipal()))
        {
            return "anonymous";
        }

        return authentication.getName();
    }

    private String getResponseStatus(int statusCode)
    {
        HttpStatus status = HttpStatus.resolve(statusCode);

        return status != null ? status.getReasonPhrase() : "Unknown";
    }
}