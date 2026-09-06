package com.springbootapigateway.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "logs")
public class Log
{
    @Id
    private String id;
    private String username;
    private String ipAddress;
    private String method;
    private String endpoint;
    private int responseCode;
    private String responseStatus;
    private boolean rateLimitViolation;
    private LocalDateTime timestamp;
}