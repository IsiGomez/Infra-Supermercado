package cl.supermercado.pago.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;


@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PagoResponseDto", description = "DTO de respuesta del pago")
public class PagoResponseDto {

    @Schema(description = "ID del pago", example = "10")
    private Long id;

    @Schema(description = "ID del usuario que realiza el pago", example = "2")
    private Long usuarioId;

    @Schema(description = "Monto del pago", example = "15990.0")
    private Double monto;

    @Schema(description = "Metodo de pago: TARJETA, CREDITO o EFECTIVO", example = "TARJETA")
    private String metodo;

    @Schema(description = "Si es que el pago fue exitoso", example = "true")
    private Boolean exitoso;

    @Schema(description = "Fecha del pago", example = "2026-07-11T14:30:00")
    private LocalDateTime fechaPago;

}
