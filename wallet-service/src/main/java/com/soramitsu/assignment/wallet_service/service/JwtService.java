package com.soramitsu.assignment.wallet_service.service;

import io.jsonwebtoken.Claims;

public interface JwtService {

    Claims extractAllClaims(String token);

    public Long extractUserId(String token);
}
