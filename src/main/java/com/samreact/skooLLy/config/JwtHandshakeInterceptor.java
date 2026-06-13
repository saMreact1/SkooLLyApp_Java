package com.samreact.skooLLy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = extractToken(servletRequest.getServletRequest());

            if (token != null && jwtService.validateToken(token)) {
                String email = jwtService.extractUsername(token);
                Long userId = jwtService.extractUserId(token);
                String role = jwtService.extractRole(token);
                Long schoolId = jwtService.extractSchoolId(token);

                attributes.put("userId", userId);
                attributes.put("email", email);
                attributes.put("role", role);
                attributes.put("schoolId", schoolId);

                log.debug("WebSocket handshake authenticated: userId={}, role={}", userId, role);
                return true;
            }
        }

        log.warn("WebSocket handshake failed: invalid or missing token");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }

    private String extractToken(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        String tokenParam = request.getParameter("token");
        if (tokenParam != null) {
            return tokenParam;
        }
        return null;
    }
}