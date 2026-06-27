package cl.supermercado.puntos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PuntosRequest", description = "DTO para asignar puntos manualmente a un usuario")
public class PuntosRequestDto {

    @Schema(description = "Id del usuario que recibe los puntos", example = "2", required = true)
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "Id de la compra que originó los puntos", example = "15", required = true)
    @NotNull(message = "La compra es obligatoria")
    private Long compraId;

    @Schema(description = "Monto total de la compra, usado para calcular los puntos " +
            "(1 punto cada $100)", example = "15000.0")
    private Double total;

}
