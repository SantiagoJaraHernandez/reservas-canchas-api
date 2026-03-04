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

    public Reserva obtener(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
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

    public Reserva actualizar(Long id, Reserva data) {
        Reserva existente = obtener(id);
        // no cambiar id, fechaCreacion o estado de forma arbitraria
        existente.setIdCancha(data.getIdCancha());
        existente.setIdUsuario(data.getIdUsuario());
        existente.setFecha(data.getFecha());
        existente.setHoraInicio(data.getHoraInicio());
        existente.setHoraFin(data.getHoraFin());
        // check solapamiento si se cambió horario
        var solapadas = reservaRepository.findSolapadas(
                existente.getIdCancha(),
                existente.getFecha(),
                existente.getHoraInicio(),
                existente.getHoraFin()
        );
        // excluir la propia reserva
        solapadas.removeIf(r -> r.getId().equals(id));
        if (!solapadas.isEmpty()) {
            throw new RuntimeException("La cancha ya está reservada en ese horario");
        }
        return reservaRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada");
        }
        reservaRepository.deleteById(id);
    }
}