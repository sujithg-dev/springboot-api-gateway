package com.springbootapigateway.controller;

import com.springbootapigateway.filter.AuthFilter;
import com.springbootapigateway.model.User;
import com.springbootapigateway.service.AuthService;
import com.springbootapigateway.service.LogService;
import com.springbootapigateway.service.RateLimitService;
import com.springbootapigateway.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private LogService logService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AuthFilter authFilter;

    @Test
    void userRegister_shouldRegisterUser_whenUsernameDoesNotExist() throws Exception
    {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "sujith",
                                    "password": "1234"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully."));

        verify(userService).addUser(any(User.class));
    }

    @Test
    void userRegister_shouldReturnBadRequest_whenUsernameAlreadyExists() throws Exception
    {
        User user = new User();
        user.setUsername("sujith");

        when(userService.findByUsername("sujith")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "sujith",
                                    "password": "1234"
                                }
                                """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists!"));

        verify(userService, times(1)).findByUsername("sujith");
        verify(userService, never()).addUser(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void userLogin_shouldReturnToken_whenCredentialsAreValid() throws Exception
    {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("sujith")
                .password("1234")
                .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authService.generateToken(userDetails)).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "sujith",
                                    "password": "1234"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("sujith logged in successfully!"))
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, times(1)).generateToken(userDetails);
    }

    @Test
    void userLogin_shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception
    {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "sujith",
                                    "password": "123456"
                                }
                                """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password!"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void userLogin_shouldReturnUnauthorized_whenAuthenticationFails() throws Exception
    {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Authentication failed") {});

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "sujith",
                                    "password": "1234"
                                }
                                """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed!"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authService, never()).generateToken(any(UserDetails.class));
    }
}