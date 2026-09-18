package com.springbootapigateway.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest
{
    @Size(min = 4, message = "Username must be at least 4 characters!")
    private String username;

    @Size(min = 4, message = "Password must be at least 4 characters!")
    private String password;
}