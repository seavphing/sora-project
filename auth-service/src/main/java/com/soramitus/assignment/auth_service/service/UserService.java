package com.soramitus.assignment.auth_service.service;

import com.soramitus.assignment.auth_service.dto.RegisterRequest;
import com.soramitus.assignment.auth_service.model.User;

import java.util.Optional;

public interface UserService {

    User createUser(RegisterRequest registerRequest);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);
}
