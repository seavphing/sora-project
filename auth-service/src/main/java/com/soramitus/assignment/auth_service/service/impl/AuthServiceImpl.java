package com.soramitus.assignment.auth_service.service.impl;

import com.soramitus.assignment.auth_service.dto.AuthResponse;
import com.soramitus.assignment.auth_service.dto.LoginRequest;
import com.soramitus.assignment.auth_service.dto.RegisterRequest;
import com.soramitus.assignment.auth_service.exception.EntityAlreadyExistsException;
import com.soramitus.assignment.auth_service.exception.InvalidCredentialsException;
import com.soramitus.assignment.auth_service.messaging.UserEventPublisher;
import com.soramitus.assignment.auth_service.model.User;
import com.soramitus.assignment.auth_service.service.AuthService;
import com.soramitus.assignment.auth_service.service.JwtService;
import com.soramitus.assignment.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher userEventPublisher;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user with username: {}", request.getUsername());

        // Check if username or email already exists
        if (userService.existsByUsername(request.getUsername())) {
            log.warn("Username already taken: {}", request.getUsername());
            throw new EntityAlreadyExistsException("Username already taken");
        }

        if (userService.existsByEmail(request.getEmail())) {
            log.warn("Email already in use: {}", request.getEmail());
            throw new EntityAlreadyExistsException("Email already in use");
        }

        // Create new user
        User savedUser = userService.createUser(request);

        // Create a wallet for the user (call wallet service)
        publishUserCreatedEvent(savedUser);

        // Generate JWT token
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getUsername());

        log.info("User registered successfully: {}", savedUser.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        // Find user by username
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("Invalid login attempt - username not found: {}", request.getUsername());
                    return new InvalidCredentialsException("Invalid username or password");
                });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid login attempt - incorrect password for user: {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getId(), user.getUsername());

        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    private void publishUserCreatedEvent(User user) {
        try {
            userEventPublisher.publishUserCreatedEvent(user);
        } catch (Exception e) {
            // Log error but don't fail registration
            log.error("Error publishing user created event for userId: {}", user.getId(), e);
        }
    }
}
