package com.axlab.Airxelerate.service.impl;

import com.axlab.Airxelerate.dto.*;
import com.axlab.Airxelerate.entity.User;
import com.axlab.Airxelerate.enums.Role;
import com.axlab.Airxelerate.exception.EmailAlreadyExistsException;
import com.axlab.Airxelerate.exception.UserAlreadyExistsException;
import com.axlab.Airxelerate.exception.UserNotFoundException;
import com.axlab.Airxelerate.repository.UserRepository;
import com.axlab.Airxelerate.security.JwtTokenProvider;
import com.axlab.Airxelerate.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;

    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("example").lastName("example")
                .username("example").email("example@example.com")
                .password("pass1234word").role(Role.USER).build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        RegisterResponse response = userService.registerUser(request);

        assertEquals("Registration successful as USER", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_usernameExists() {
        RegisterRequest request = RegisterRequest.builder().username("example").build();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_emailExists() {
        RegisterRequest request = RegisterRequest.builder().username("example").email("test@test.com").build();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("example", "pass1234word");
        User user = User.builder().username("example").role(Role.USER).build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByUsername("example")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken("example", "USER")).thenReturn("jwtToken");

        LoginResponse response = userService.login(request);

        assertEquals("jwtToken", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void login_invalidCredentials() {
        LoginRequest request = new LoginRequest("example", "wrongpass");
        doThrow(BadCredentialsException.class)
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> userService.login(request));
    }

    @Test
    void login_userNotFound() {
        LoginRequest request = new LoginRequest("example", "pass1234word");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(UsernamePasswordAuthenticationToken.class));
        when(userRepository.findByUsername("example")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.login(request));
    }

    @Test
    void deleteUser_success() {
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void findAll_usersExist() {
        User user = User.builder().id(1L).firstName("example").lastName("example")
                .username("example").email("example@example.com").role(Role.USER).build();
        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<UserDto> result = userService.findAll(PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals("example", result.getContent().get(0).getUsername());
    }

    @Test
    void findAll_noUsers() {
        Page<User> page = Page.empty();
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<UserDto> result = userService.findAll(PageRequest.of(0, 10));
        assertTrue(result.isEmpty());
    }
}
