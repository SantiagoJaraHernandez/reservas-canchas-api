package com.reservas.ms_reservas.dto;

import com.reservas.ms_reservas.model.Cancha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanchaDTO {

    private Long id;

    @NotBlank(message = "El nombre de la cancha es requerido")
    private String nombre;

    @NotBlank(message = "El tipo de cancha es requerido")
    private String tipo;

    @Positive(message = "El precio por hora debe ser mayor a 0")
    private Double precioHora;

    private Boolean activa;

    private String descripcion;

    public static CanchaDTO fromEntity(Cancha cancha) {
        return new CanchaDTO(
                cancha.getId(),
                cancha.getNombre(),
                cancha.getTipo(),
                cancha.getPrecioHora(),
                cancha.getActiva(),
                cancha.getDescripcion()
        );
    }

    public Cancha toEntity() {
        Cancha cancha = new Cancha();
        cancha.setId(this.id);
        cancha.setNombre(this.nombre);
        cancha.setTipo(this.tipo);
        cancha.setPrecioHora(this.precioHora);
        cancha.setActiva(this.activa != null ? this.activa : true);
        cancha.setDescripcion(this.descripcion);
        return cancha;
    }
}
