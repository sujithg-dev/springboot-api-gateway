package com.springbootapigateway.service;

import com.springbootapigateway.model.User;
import com.springbootapigateway.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService
{
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public void addUser(User user)
    {
        userRepository.save(user);
    }
}