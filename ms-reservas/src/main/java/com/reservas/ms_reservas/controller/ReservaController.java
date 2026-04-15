package com.reservas.ms_reservas.controller;

import com.reservas.ms_reservas.dto.ReservaDTO;
import com.reservas.ms_reservas.model.Reserva;
import com.reservas.ms_reservas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Slf4j
public class ReservaController {

    private final ReservaService reservaService;

    private static final String ADMIN_ROLE = "ADMIN";

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        
        log.info("GET /reservas - User: {}, Role: {}", userId, role);
        
        if (ADMIN_ROLE.equals(role)) {
            log.info("ADMIN user {} listing all reservas", userId);
            List<ReservaDTO> reservas = reservaService.listar().stream()
                    .map(ReservaDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservas);
        } else if (userId != null) {
            log.info("USER {} listing their own reservas", userId);
            List<ReservaDTO> reservas = reservaService.listar().stream()
                    .filter(r -> r.getIdUsuario().equals(Long.parseLong(userId)))
                    .map(ReservaDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(reservas);
        } else {
            log.warn("Unauthorized access attempt to list reservas. No role or userId header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtener(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        
        log.info("GET /reservas/{} - User: {}, Role: {}", id, userId, role);
        
        Reserva reserva = reservaService.obtener(id);
        
        // ADMIN puede ver cualquier reserva
        if (ADMIN_ROLE.equals(role)) {
            return ResponseEntity.ok(ReservaDTO.fromEntity(reserva));
        }
        
        // USER solo puede ver sus propias reservas
        if (userId != null && reserva.getIdUsuario().equals(Long.parseLong(userId))) {
            return ResponseEntity.ok(ReservaDTO.fromEntity(reserva));
        }
        
        log.warn("USER {} attempted to access reserva {} of another user", userId, id);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(
            @Valid @RequestBody ReservaDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        log.info("POST /reservas - User: {}, Cancha: {}", userId, dto.getIdCancha());
        
        if (userId == null) {
            log.warn("Unauthorized attempt to create reserva. No userId header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        dto.setIdUsuario(Long.parseLong(userId));
        Reserva entidad = dto.toEntity();
        Reserva guardada = reservaService.crear(entidad);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ReservaDTO.fromEntity(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReservaDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        
        log.info("PUT /reservas/{} - User: {}, Role: {}", id, userId, role);
        
        Reserva reserva = reservaService.obtener(id);
        
        // ADMIN puede actualizar cualquier reserva
        if (!ADMIN_ROLE.equals(role) && (userId == null || !reserva.getIdUsuario().equals(Long.parseLong(userId)))) {
            log.warn("USER {} attempted to update reserva {} of another user", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        dto.setIdUsuario(reserva.getIdUsuario()); // Preservar usuario original
        Reserva entidad = dto.toEntity();
        Reserva actualizada = reservaService.actualizar(id, entidad);
        return ResponseEntity.ok(ReservaDTO.fromEntity(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        
        log.info("DELETE /reservas/{} - User: {}, Role: {}", id, userId, role);
        
        Reserva reserva = reservaService.obtener(id);
        
        // ADMIN puede eliminar cualquier reserva
        if (!ADMIN_ROLE.equals(role) && (userId == null || !reserva.getIdUsuario().equals(Long.parseLong(userId)))) {
            log.warn("USER {} attempted to delete reserva {} of another user", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }