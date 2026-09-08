package com.springbootapigateway.config;

import com.springbootapigateway.filter.AuthFilter;
import com.springbootapigateway.filter.LogFilter;
import com.springbootapigateway.filter.RateLimitFilter;
import com.springbootapigateway.service.UserDetailsServiceImplementation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig
{
    private final AuthFilter authFilter;
    private final RateLimitFilter rateLimitFilter;
    private final LogFilter logFilter;
    private final UserDetailsServiceImplementation userDetailsServiceImplementation;
    private final AuthenticationEntryPointImplementation authenticationEntryPointImplementation;

    public SecurityConfig(AuthFilter authFilter, RateLimitFilter rateLimitFilter, LogFilter logFilter, UserDetailsServiceImplementation userDetailsServiceImplementation, AuthenticationEntryPointImplementation authenticationEntryPointImplementation)
    {
        this.authFilter = authFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.logFilter = logFilter;
        this.userDetailsServiceImplementation = userDetailsServiceImplementation;
        this.authenticationEntryPointImplementation = authenticationEntryPointImplementation;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
    {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/actuator/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPointImplementation))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(logFilter, AuthFilter.class)
                .addFilterAfter(rateLimitFilter, LogFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsServiceImplementation);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
    {
        return configuration.getAuthenticationManager();
    }
}