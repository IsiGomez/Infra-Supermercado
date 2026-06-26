package cl.duoc.carrito.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PromocionDto", description = "DTO de comunicacion con microservicio promociones")
public class PromocionDto {

    @Schema(description = "Id de la promocion")
    private Long id;

    @Schema(description = "Codigo de la promocion")
    private String codigo;

    @Schema(description = "Porcentaje de descuento")
    private Double descuento;

    @Schema(description = "Fecha de inicio")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de vencimiento")
    private LocalDate fechaFin;

    @Schema(description = "Consulta de si es acumulable")
    private Boolean acumulable;

    @Schema(description = "Confirmacion de vigencia")
    public boolean isVigente() {
        LocalDate hoy = LocalDate.now();
        return fechaInicio != null && fechaFin != null
                && !hoy.isBefore(fechaInicio)
                && !hoy.isAfter(fechaFin);
    }

}
