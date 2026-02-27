package com.reservas.ms_reservas.controller;

import com.reservas.ms_reservas.model.Reserva;
import com.reservas.ms_reservas.service.ReservaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> listar() {
        return reservaService.listar();
    }

    @PostMapping
    public Reserva crear(@RequestBody Reserva reserva) {
        return reservaService.crear(reserva);
    }
}