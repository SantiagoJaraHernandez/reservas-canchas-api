package com.reservas.ms_reservas.controller;

import com.reservas.ms_reservas.dto.ReservaDTO;
import com.reservas.ms_reservas.model.Reserva;
import com.reservas.ms_reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<ReservaDTO> listar() {
        return reservaService.listar().stream()
                .map(ReservaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReservaDTO obtener(@PathVariable Long id) {
        return ReservaDTO.fromEntity(reservaService.obtener(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaDTO crear(@Valid @RequestBody ReservaDTO dto) {
        Reserva entidad = dto.toEntity();
        Reserva guardada = reservaService.crear(entidad);
        return ReservaDTO.fromEntity(guardada);
    }

    @PutMapping("/{id}")
    public ReservaDTO actualizar(@PathVariable Long id,
                                 @Valid @RequestBody ReservaDTO dto) {
        Reserva entidad = dto.toEntity();
        Reserva actualizada = reservaService.actualizar(id, entidad);
        return ReservaDTO.fromEntity(actualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
    }
}