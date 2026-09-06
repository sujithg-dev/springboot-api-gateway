package com.springbootapigateway.service;

import com.springbootapigateway.model.User;
import com.springbootapigateway.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findByUsername_shouldReturnUser_whenUserExists()
    {
        String username = "sujith";

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenUserDoesNotExist()
    {
        String username = "kishore";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void addUser_shouldSaveUser()
    {
        User user = new User();
        user.setUsername("sujith");
        user.setPassword("1234");
        userService.addUser(user);

        verify(userRepository, times(1)).save(user);
    }
}
