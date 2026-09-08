package com.springbootapigateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest
{
    private AuthService authService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp()
    {
        authService = new AuthService("VGhpc0lzQVN1ZmZpY2llbnRMb25nU2VjcmV0S2V5MTIzNDU2Nzg=");
        userDetails = User.withUsername("sujith").password("1234").build();
    }

    @Test
    void generateToken_shouldReturnToken()
    {
        String token = authService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUserName_shouldReturnUsernameFromToken()
    {
        String token = authService.generateToken(userDetails);
        String username = authService.extractUserName(token);

        assertEquals("sujith", username);
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenIsValid()
    {
        String token = authService.generateToken(userDetails);
        boolean result = authService.isTokenValid(token, userDetails);

        assertTrue(result);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch()
    {
        String token = authService.generateToken(userDetails);
        UserDetails differentUser = User.withUsername("kishore").password("1234").build();
        boolean result = authService.isTokenValid(token, differentUser);

        assertFalse(result);
    }

    @Test
    void isTokenExpired_shouldReturnFalse_forNewToken()
    {
        String token = authService.generateToken(userDetails);
        boolean result = authService.isTokenExpired(token);

        assertFalse(result);
    }

    @Test
    void generateToken_shouldContainCorrectUsername()
    {
        String token = authService.generateToken(userDetails);
        String username = authService.extractUserName(token);

        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void generateToken_shouldHaveJwtFormat()
    {
        String token = authService.generateToken(userDetails);
        String[] parts = token.split("\\.");

        assertEquals(3, parts.length);
    }
}