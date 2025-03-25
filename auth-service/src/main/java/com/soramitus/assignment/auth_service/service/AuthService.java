package com.soramitus.assignment.auth_service.service;

import com.soramitus.assignment.auth_service.dto.AuthResponse;
import com.soramitus.assignment.auth_service.dto.LoginRequest;
import com.soramitus.assignment.auth_service.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest request);

    boolean validateToken(String token);
}
