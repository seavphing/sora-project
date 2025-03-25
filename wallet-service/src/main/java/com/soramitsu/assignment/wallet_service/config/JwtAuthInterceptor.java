package com.soramitsu.assignment.wallet_service.config;

import com.soramitsu.assignment.wallet_service.exception.UnauthorizedException;
import com.soramitsu.assignment.wallet_service.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Extract user ID and store in request attributes for later use
            Long userId = jwtService.extractUserId(token);
            request.setAttribute("userId", userId);
            log.debug("User ID from token: {}", userId);

            return true;
        }

        throw new UnauthorizedException("Invalid or missing token");
    }
}
