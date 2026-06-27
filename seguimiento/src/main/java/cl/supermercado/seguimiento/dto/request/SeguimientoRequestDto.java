package cl.supermercado.seguimiento.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "SeguimientoRequest", description = "DTO para registrar un cambio de estado en una compra")
public class SeguimientoRequestDto {

    @Schema(description = "Id de la compra asociada", example = "15", required = true)
    @NotNull(message = "La compra es obligatoria")
    private Long compraId;

    @Schema(description = "Id del usuario dueño de la compra", example = "2", required = true)
    @NotNull(message = "El usuario es obligatorio")
    @Positive(message = "El usuario debe ser válido")
    private Long usuarioId;

    @Schema(description = "Nuevo estado del seguimiento", example = "ENVIADO",
            allowableValues = {"PENDIENTE", "PREPARACION", "ENVIADO", "ENTREGADO"}, required = true)
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

}
