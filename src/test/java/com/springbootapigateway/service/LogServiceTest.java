package com.springbootapigateway.service;

import com.springbootapigateway.model.Log;
import com.springbootapigateway.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest
{
    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogService logService;

    @Test
    void addLog_shouldSaveLog()
    {
        Log log = new Log();
        logService.addLog(log);

        verify(logRepository, times(1)).save(log);
    }
}