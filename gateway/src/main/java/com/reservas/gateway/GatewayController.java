package com.reservas.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import com.reservas.gateway.exception.GatewayException;
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

    // ============ AUTH ROUTES ============

    @PostMapping("/auth/register")
    public Mono<ResponseEntity<String>> register(@RequestBody String body) {
        log.info("Routing POST /auth/register to ms-auth");
        return proxyRequest("POST", msAuthUrl + "/auth/register", body);
    }

    @PostMapping("/auth/login")
    public Mono<ResponseEntity<String>> login(@RequestBody String body) {
        log.info("Routing POST /auth/login to ms-auth");
        return proxyRequest("POST", msAuthUrl + "/auth/login", body);
    }

    // ============ RESERVAS ROUTES ============

    @GetMapping("/reservas")
    public Mono<ResponseEntity<String>> listarReservas() {
        log.info("Routing GET /reservas to ms-reservas");
        return webClient.get()
                .uri(msReservasUrl + "/reservas")
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "GET /reservas"));
    }

    @GetMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> obtenerReserva(@PathVariable Long id) {
        log.info("Routing GET /reservas/{} to ms-reservas", id);
        return webClient.get()
                .uri(msReservasUrl + "/reservas/{id}", id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "GET /reservas/" + id));
    }

    @PostMapping("/reservas")
    public Mono<ResponseEntity<String>> crearReserva(@RequestBody String body) {
        log.info("Routing POST /reservas to ms-reservas");
        return proxyRequest("POST", msReservasUrl + "/reservas", body);
    }

    @PutMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> actualizarReserva(@PathVariable Long id,
                                                           @RequestBody String body) {
        log.info("Routing PUT /reservas/{} to ms-reservas", id);
        return webClient.put()
                .uri(msReservasUrl + "/reservas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "PUT /reservas/" + id));
    }

    @DeleteMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> eliminarReserva(@PathVariable Long id) {
        log.info("Routing DELETE /reservas/{} to ms-reservas", id);
        return webClient.delete()
                .uri(msReservasUrl + "/reservas/{id}", id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "DELETE /reservas/" + id));
    }

    // ============ CANCHAS ROUTES ============

    @GetMapping("/canchas")
    public Mono<ResponseEntity<String>> listarCanchas() {
        log.info("Routing GET /canchas to ms-reservas");
        return webClient.get()
                .uri(msReservasUrl + "/canchas")
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "GET /canchas"));
    }

    @GetMapping("/canchas/{id}")
    public Mono<ResponseEntity<String>> obtenerCancha(@PathVariable Long id) {
        log.info("Routing GET /canchas/{} to ms-reservas", id);
        return webClient.get()
                .uri(msReservasUrl + "/canchas/{id}", id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "GET /canchas/" + id));
    }

    @PostMapping("/canchas")
    public Mono<ResponseEntity<String>> crearCancha(@RequestBody String body) {
        log.info("Routing POST /canchas to ms-reservas");
        return proxyRequest("POST", msReservasUrl + "/canchas", body);
    }

    @PutMapping("/canchas/{id}")
    public Mono<ResponseEntity<String>> actualizarCancha(@PathVariable Long id,
                                                         @RequestBody String body) {
        log.info("Routing PUT /canchas/{} to ms-reservas", id);
        return webClient.put()
                .uri(msReservasUrl + "/canchas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "PUT /canchas/" + id));
    }

    @DeleteMapping("/canchas/{id}")
    public Mono<ResponseEntity<String>> eliminarCancha(@PathVariable Long id) {
        log.info("Routing DELETE /canchas/{} to ms-reservas", id);
        return webClient.delete()
                .uri(msReservasUrl + "/canchas/{id}", id)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, "DELETE /canchas/" + id));
    }

    // ============ HELPER METHODS ============

    private Mono<ResponseEntity<String>> proxyRequest(String method, String uri, String body) {
        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class)
                .onErrorResume(ex -> handleError(ex, method + " " + uri));
    }

    private Mono<ResponseEntity<String>> handleError(Throwable ex, String endpoint) {
        log.error("Error routing {}: {}", endpoint, ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\": \"Error contacting service\"}"));
    }
}