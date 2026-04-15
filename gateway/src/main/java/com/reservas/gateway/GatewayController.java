package com.reservas.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5176"})
@RequiredArgsConstructor
@Slf4j
public class GatewayController {

    private final WebClient webClient;

    @Value("${ms-reservas.base-url:http://localhost:8081}")
    private String msReservasUrl;

    @Value("${ms-auth.base-url:http://localhost:8082}")
    private String msAuthUrl;

    @PostMapping("/auth/register")
    public Mono<ResponseEntity<String>> register(@RequestBody String body) {
        return proxyPost(msAuthUrl + "/auth/register", body);
    }

    @PostMapping("/auth/register-admin")
    public Mono<ResponseEntity<String>> registerAdmin(@RequestBody String body) {
        return proxyPost(msAuthUrl + "/auth/register-admin", body);
    }

    @PostMapping("/auth/login")
    public Mono<ResponseEntity<String>> login(@RequestBody String body) {
        return proxyPost(msAuthUrl + "/auth/login", body);
    }

    @GetMapping("/reservas")
    public Mono<ResponseEntity<String>> listarReservas(ServerWebExchange exchange) {
        return webClient.get()
                .uri(msReservasUrl + "/reservas")
                .headers(h -> copyAuthHeaders(exchange, h))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @GetMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> obtenerReserva(@PathVariable Long id, ServerWebExchange exchange) {
        return webClient.get()
                .uri(msReservasUrl + "/reservas/{id}", id)
                .headers(h -> copyAuthHeaders(exchange, h))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @PostMapping("/reservas")
    public Mono<ResponseEntity<String>> crearReserva(@RequestBody String body, ServerWebExchange exchange) {
        return proxyPostWithAuth(msReservasUrl + "/reservas", body, exchange);
    }

    @PutMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> actualizarReserva(@PathVariable Long id,
                                                          @RequestBody String body,
                                                          ServerWebExchange exchange) {
        return webClient.put()
                .uri(msReservasUrl + "/reservas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> copyAuthHeaders(exchange, h))
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @DeleteMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> eliminarReserva(@PathVariable Long id, ServerWebExchange exchange) {
        return webClient.delete()
                .uri(msReservasUrl + "/reservas/{id}", id)
                .headers(h -> copyAuthHeaders(exchange, h))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @GetMapping("/canchas")
    public Mono<ResponseEntity<String>> listarCanchas() {
        return webClient.get()
                .uri(msReservasUrl + "/canchas")
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @GetMapping("/canchas/activas")
    public Mono<ResponseEntity<String>> listarCanchasActivas() {
        return webClient.get()
                .uri(msReservasUrl + "/canchas/activas")
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @GetMapping("/canchas/{id}")
    public Mono<ResponseEntity<String>> obtenerCancha(@PathVariable Long id) {
        return webClient.get()
                .uri(msReservasUrl + "/canchas/{id}", id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @PostMapping("/canchas")
    public Mono<ResponseEntity<String>> crearCancha(@RequestBody String body, ServerWebExchange exchange) {
        return proxyPostWithAuth(msReservasUrl + "/canchas", body, exchange);
    }

    @PutMapping("/canchas/{id}")
    public Mono<ResponseEntity<String>> actualizarCancha(@PathVariable Long id,
                                                         @RequestBody String body,
                                                         ServerWebExchange exchange) {
        return webClient.put()
                .uri(msReservasUrl + "/canchas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> copyAuthHeaders(exchange, h))
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    @DeleteMapping("/canchas/{id}")
    public Mono<ResponseEntity<String>> eliminarCancha(@PathVariable Long id, ServerWebExchange exchange) {
        return webClient.delete()
                .uri(msReservasUrl + "/canchas/{id}", id)
                .headers(h -> copyAuthHeaders(exchange, h))
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    private Mono<ResponseEntity<String>> proxyPost(String uri, String body) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    private Mono<ResponseEntity<String>> proxyPostWithAuth(String uri, String body, ServerWebExchange exchange) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> copyAuthHeaders(exchange, h))
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(this::handleError);
    }

    private void copyAuthHeaders(ServerWebExchange exchange, HttpHeaders headers) {
        headers.set("X-User-Id", valueOrEmpty(exchange, "X-User-Id"));
        headers.set("X-User-Email", valueOrEmpty(exchange, "X-User-Email"));
        headers.set("X-User-Role", valueOrEmpty(exchange, "X-User-Role"));
    }

    private String valueOrEmpty(ServerWebExchange exchange, String headerName) {
        String value = exchange.getRequest().getHeaders().getFirst(headerName);
        return value != null ? value : "";
    }

    private Mono<ResponseEntity<String>> handleError(Throwable ex) {
        if (ex instanceof WebClientResponseException wex) {
            log.error("Downstream error: {} {}", wex.getStatusCode(), wex.getResponseBodyAsString());
            return Mono.just(
                    ResponseEntity.status(wex.getStatusCode())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(wex.getResponseBodyAsString())
            );
        }

        log.error("Unexpected gateway error", ex);
        return Mono.just(
                ResponseEntity.internalServerError()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"Error contacting service\"}")
        );
    }
}