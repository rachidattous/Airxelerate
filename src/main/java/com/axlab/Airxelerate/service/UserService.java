package com.axlab.Airxelerate.service;

import com.axlab.Airxelerate.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    RegisterResponse registerUser(RegisterRequest registerRequest);
    Page<UserDto> findAll(Pageable pageable);
    LoginResponse login(LoginRequest request);
    void deleteUser(Long id);
}
