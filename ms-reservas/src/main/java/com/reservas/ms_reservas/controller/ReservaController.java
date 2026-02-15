package com.reservas.ms_reservas.controller;

import com.reservas.ms_reservas.model.Reserva;
import com.reservas.ms_reservas.repository.ReservaRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaRepository reservaRepository;

    public ReservaController(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @GetMapping
    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    @PostMapping
    public Reserva crear(@RequestBody Reserva reserva) {
        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.now());
        return reservaRepository.save(reserva);
    }
}
