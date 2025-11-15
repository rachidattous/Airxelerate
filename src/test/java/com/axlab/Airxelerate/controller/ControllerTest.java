package com.axlab.Airxelerate.controller;

import com.axlab.Airxelerate.dto.*;
import com.axlab.Airxelerate.enums.Role;
import com.axlab.Airxelerate.exception.EmailAlreadyExistsException;
import com.axlab.Airxelerate.exception.UserAlreadyExistsException;
import com.axlab.Airxelerate.security.JwtTokenProvider;
import com.axlab.Airxelerate.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AuthController.class, UserController.class})
@AutoConfigureMockMvc(addFilters = false)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        when(jwtTokenProvider.validateToken(any())).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(any())).thenReturn("example");
        when(jwtTokenProvider.getRoleFromToken(any())).thenReturn(Role.USER.name());

        registerRequest = RegisterRequest.builder()
                .firstName("example")
                .lastName("example")
                .username("example")
                .email("example@example.com")
                .password("pass1234word")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_success() throws Exception {
        RegisterResponse response = RegisterResponse.builder()
                .message("Registration successful as USER").build();

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful as USER"));
    }

    @Test
    void register_usernameExists() throws Exception {
        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("example"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with username example already exists."));
    }

    @Test
    void register_emailExists() throws Exception {
        when(userService.registerUser(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("example@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("A user with email example@example.com already exists."));
    }

    @Test
    void login_success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("example", "pass1234word");
        LoginResponse loginResponse = LoginResponse.builder()
                .token("jwtToken")
                .message("Login successful")
                .build();

        when(userService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_success() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .firstName("example")
                .lastName("example")
                .username("example")
                .email("example@example.com")
                .role(Role.USER)
                .build();
        Page<UserDto> page = new PageImpl<>(List.of(userDto));

        when(userService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("example"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_success() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}