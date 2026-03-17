package com.reservas.ms_reservas.dto;

import com.reservas.ms_reservas.model.Reserva;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservaDTO {

    private Long id;

    @NotNull
    private Long idUsuario;

    @NotNull
    private Long idCancha;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFin;

    private String estado;

    private LocalDateTime fechaCreacion;

    // getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCancha() {
        return idCancha;
    }

    public void setIdCancha(Long idCancha) {
        this.idCancha = idCancha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public static ReservaDTO fromEntity(Reserva r) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(r.getId());
        dto.setIdUsuario(r.getIdUsuario());
        dto.setIdCancha(r.getIdCancha());
        dto.setFecha(r.getFecha());
        dto.setHoraInicio(r.getHoraInicio());
        dto.setHoraFin(r.getHoraFin());
        dto.setEstado(r.getEstado());
        dto.setFechaCreacion(r.getFechaCreacion());
        return dto;
    }

    public Reserva toEntity() {
        Reserva r = new Reserva();
        r.setId(this.id);
        r.setIdUsuario(this.idUsuario);
        r.setIdCancha(this.idCancha);
        r.setFecha(this.fecha);
        r.setHoraInicio(this.horaInicio);
        r.setHoraFin(this.horaFin);
        r.setEstado(this.estado);
        r.setFechaCreacion(this.fechaCreacion);
        return r;
    }
}
