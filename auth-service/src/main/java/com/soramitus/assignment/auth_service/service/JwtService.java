package com.soramitus.assignment.auth_service.service;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    Date extractExpiration(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(Long userId, String username);

    String generateToken(Map<String, Object> extraClaims, Long userId, String username);

    Boolean validateToken(String token);

    Claims extractAllClaims(String token);
}
