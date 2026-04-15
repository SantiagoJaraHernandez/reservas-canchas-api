package com.reservas.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/auth/register",
            "/auth/login",
            "/canchas"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Rutas públicas, permitir sin token
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        // Validar token en rutas protegidas
        String token = extractToken(exchange);

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            log.warn("Unauthorized access attempt to: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // Token válido, extraer información y propagar headers
        String email = jwtTokenProvider.getEmailFromToken(token);
        String role = jwtTokenProvider.getRoleFromToken(token);
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        log.info("Token valid for user: {} with role: {}", email, role);

        // Crear nuevo request con headers propagados
        ServerWebExchange newExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-User-Email", email)
                        .header("X-User-Id", userId)
                        .header("X-User-Role", role)
                        .build())
                .build();

        return chain.filter(newExchange);
    }

    private String extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }
}
