package com.reservas.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.reservas.gateway.exception.GatewayException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5176"})
public class GatewayController {

    private final WebClient webClient;

    public GatewayController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/reservas")
    public Mono<ResponseEntity<String>> listarReservas() {
        return webClient.get()
                .uri("/reservas")
                .retrieve()
                .toEntity(String.class);
    }

    @GetMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> obtenerReserva(@PathVariable Long id) {
        return webClient.get()
                .uri("/reservas/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new GatewayException(response.statusCode().value(), errorBody))))
                .toEntity(String.class)
                .onErrorResume(GatewayException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatus()).body(ex.getBody())));
    }

    @PostMapping("/reservas")
    public Mono<ResponseEntity<String>> crearReserva(@RequestBody String body) {
        return webClient.post()
                .uri("/reservas")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new GatewayException(response.statusCode().value(), errorBody))))
                .toEntity(String.class)
                .onErrorResume(GatewayException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatus()).body(ex.getBody())));
    }

    @PutMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> actualizarReserva(@PathVariable Long id,
                                                           @RequestBody String body) {
        return webClient.put()
                .uri("/reservas/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new GatewayException(response.statusCode().value(), errorBody))))
                .toEntity(String.class)
                .onErrorResume(GatewayException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatus()).body(ex.getBody())));
    }

    @DeleteMapping("/reservas/{id}")
    public Mono<ResponseEntity<String>> eliminarReserva(@PathVariable Long id) {
        return webClient.delete()
                .uri("/reservas/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(
                                        new GatewayException(response.statusCode().value(), errorBody))))
                .toEntity(String.class)
                .onErrorResume(GatewayException.class, ex ->
                        Mono.just(ResponseEntity.status(ex.getStatus()).body(ex.getBody())));
    }
}