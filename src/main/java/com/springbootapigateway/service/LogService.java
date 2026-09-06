package com.springbootapigateway.service;

import com.springbootapigateway.model.Log;
import com.springbootapigateway.repository.LogRepository;
import org.springframework.stereotype.Service;

@Service
public class LogService
{
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository)
    {
        this.logRepository = logRepository;
    }

    public void addLog(Log log)
    {
        logRepository.save(log);
    }
}