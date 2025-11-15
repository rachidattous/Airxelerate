package com.axlab.Airxelerate.service.Impl;

import com.axlab.Airxelerate.dto.*;
import com.axlab.Airxelerate.entity.User;
import com.axlab.Airxelerate.enums.Role;
import com.axlab.Airxelerate.exception.EmailAlreadyExistsException;
import com.axlab.Airxelerate.exception.UserAlreadyExistsException;
import com.axlab.Airxelerate.exception.UserNotFoundException;
import com.axlab.Airxelerate.repository.UserRepository;
import com.axlab.Airxelerate.security.JwtTokenProvider;
import com.axlab.Airxelerate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        log.info("Attempting to register user= {}", request.getUsername());

        if(userRepository.existsByUsername(request.getUsername())){
            log.warn("Registration Failed. Username already exists: {}", request.getUsername());
            throw new UserAlreadyExistsException(request.getUsername());
        }

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("Registration Failed. Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();

        userRepository.save(user);

        log.info("User {} registered as {}", user.getUsername(), user.getRole());

        return RegisterResponse.builder()
                .message("Registration successful as " + user.getRole())
                .build();
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        log.debug("Fetch users with pagination: page={}, size={}, sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<User> page = userRepository.findAll(pageable);

        if(page.isEmpty()){
            log.warn("No users found for the requested page.");
            return Page.empty();
        }

        return page.map(user -> UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for username {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for username {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found: {}", request.getUsername());
                    return new UserNotFoundException(request.getUsername());
                });

        String token = jwtTokenProvider.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        log.info("User {} successfully authenticated", request.getUsername());

        return LoginResponse.builder()
                .token(token)
                .message("Login successful")
                .build();
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
        log.warn("User with id: {} deleted successfully", user.getId());
    }
}
