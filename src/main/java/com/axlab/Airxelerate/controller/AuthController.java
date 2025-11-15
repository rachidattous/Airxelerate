package com.axlab.Airxelerate.controller;

import com.axlab.Airxelerate.dto.*;
import com.axlab.Airxelerate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Log4j2
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.warn("Validation error during registration: {}", bindingResult.getFieldErrors().get(0).getDefaultMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(bindingResult.getFieldErrors().get(0).getDefaultMessage())
                    );
        }

        RegisterResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            String errorMsg = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            log.warn("Validation error during login: {}", errorMsg);
            return ResponseEntity.badRequest()
                    .body(LoginResponse.builder()
                            .message(errorMsg)
                            .build());
        }

        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
