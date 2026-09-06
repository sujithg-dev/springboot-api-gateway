package com.springbootapigateway.controller;

import com.springbootapigateway.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest
{
    @Autowired private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private LogService logService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private UserDetailsServiceImplementation userDetailsServiceImplementation;

    @MockitoBean
    private Authentication authentication;

    @Test
    void profile_shouldReturnUsername() throws Exception
    {
        when(authentication.getName()).thenReturn("sujith");

        mockMvc.perform(get("/api/user/profile")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello sujith"));
    }
}

