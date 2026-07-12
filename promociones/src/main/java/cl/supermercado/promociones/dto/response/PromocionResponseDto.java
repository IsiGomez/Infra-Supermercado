package cl.supermercado.promociones.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDate;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PromocionResponseDto", description = "DTO de respuesta de promoción")
public class PromocionResponseDto {

    @Schema(description = "Id de la promoción", example = "5")
    private Long id;

    @Schema(description = "Codigo de la promoción", example = "PROMO10")
    private String codigo;

    @Schema(description = "Descuento que realiza la promoción", example = "10.0")
    private Double descuento;

    @Schema(description = "Fecha de Inicio de la promocion", example = "2026-07-01")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de vencimiento de la promoción", example = "2026-07-31")
    private LocalDate fechaFin;

    @Schema(description = "Si es que es acumulable", example = "true")
    private Boolean acumulable;

}