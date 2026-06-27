package cl.supermercado.promociones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PromocionRequest", description = "DTO para crear una promoción")
public class PromocionRequestDto {

    @Schema(description = "Código único de la promoción", example = "PROMO10", required = true)
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @Schema(description = "Porcentaje de descuento a aplicar (0-100)", example = "10.0", required = true)
    @NotNull(message = "El descuento es obligatorio")
    @Positive(message = "El descuento debe ser mayor a 0")
    private Double descuento;

    @Schema(description = "Fecha desde la cual la promoción es válida", example = "2026-06-01", required = true)
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha hasta la cual la promoción es válida", example = "2026-12-31", required = true)
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @Schema(description = "Indica si se puede combinar con otras promociones", example = "false", required = true)
    @NotNull(message = "Debe indicar si es acumulable")
    private Boolean acumulable;

}
