package com.springbootapigateway.controller;

import com.springbootapigateway.dto.LoginRequest;
import com.springbootapigateway.dto.LoginResponse;
import com.springbootapigateway.dto.RegisterRequest;
import com.springbootapigateway.model.User;
import com.springbootapigateway.service.AuthService;
import com.springbootapigateway.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController
{
    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager)
    {
        this.authService = authService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> userRegister(@RequestBody RegisterRequest registerRequest)
    {
        if (userService.findByUsername(registerRequest.getUsername()).isPresent())
        {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", HttpStatus.BAD_REQUEST.value() + " - " + HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "message", "Username already exists!"
                    ));
        }

        else
        {
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            userService.addUser(user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(Map.of(
                            "status", HttpStatus.OK.value() + " - " + HttpStatus.OK.getReasonPhrase(),
                            "message", "User registered successfully."
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginRequest loginRequest)
    {
        try
        {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = authService.generateToken(userDetails);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new LoginResponse(
                            HttpStatus.OK.value() + " - " + HttpStatus.OK.getReasonPhrase(),
                            loginRequest.getUsername() + " logged in successfully.",
                            token));
        }
        catch (BadCredentialsException e)
        {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value() + " - " + HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            "message", "Invalid username or password!"
                    ));
        }
        catch (AuthenticationException e)
        {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value() + " - " + HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            "message", "Authentication failed!"
                    ));
        }
    }
}