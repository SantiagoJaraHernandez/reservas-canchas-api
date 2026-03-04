package com.reservas.gateway;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5176"})
public class GatewayController {

    private final WebClient webClient;

    public GatewayController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @GetMapping("/reservas")
    public Mono<String> listarReservas() {
        return webClient.get()
                .uri("/reservas")
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/reservas/{id}")
    public Mono<String> obtenerReserva(@PathVariable Long id) {
        return webClient.get()
                .uri("/reservas/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping("/reservas")
    public Mono<String> crearReserva(@RequestBody String body) {
        return webClient.post()
                .uri("/reservas")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    @PutMapping("/reservas/{id}")
    public Mono<String> actualizarReserva(@PathVariable Long id, @RequestBody String body) {
        return webClient.put()
                .uri("/reservas/{id}", id)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    @DeleteMapping("/reservas/{id}")
    public Mono<Void> eliminarReserva(@PathVariable Long id) {
        return webClient.delete()
                .uri("/reservas/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}