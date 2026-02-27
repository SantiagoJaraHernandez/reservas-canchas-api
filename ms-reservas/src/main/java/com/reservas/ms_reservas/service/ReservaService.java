package com.reservas.ms_reservas.service;

import com.reservas.ms_reservas.model.Reserva;
import com.reservas.ms_reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva crear(Reserva reserva) {

        var solapadas = reservaRepository.findSolapadas(
                reserva.getIdCancha(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin()
        );

        if (!solapadas.isEmpty()) {
            throw new RuntimeException("La cancha ya está reservada en ese horario");
        }

        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.now());

        return reservaRepository.save(reserva);
    }
}