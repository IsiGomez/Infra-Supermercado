package cl.supermercado.pago.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PagoRequest", description = "DTO para procesar un pago")
public class PagoRequestDto {

    @Schema(description = "ID del usuario que paga (debe coincidir con el del JWT)", example = "2", required = true)
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "Método de pago", example = "TARJETA",
            allowableValues = {"TARJETA", "CREDITO", "EFECTIVO"}, required = true)
    @NotBlank(message = "El ingreso de un método de pago es obligatorio")
    private String metodo;

}
