package com.doubao.order.controller;

import com.doubao.order.common.ApiResponse;
import com.doubao.order.dto.JwtResponse;
import com.doubao.order.dto.LoginRequest;
import com.doubao.order.dto.RegisterRequest;
import com.doubao.order.entity.User;
import com.doubao.order.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ApiResponse.success("User registered successfully", user);
    }

    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ApiResponse.success("Login successful", response);
    }
}
