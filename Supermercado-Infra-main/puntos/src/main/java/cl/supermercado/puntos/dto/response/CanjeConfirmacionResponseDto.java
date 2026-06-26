package cl.supermercado.puntos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CanjeConfirmacionResponse", description = "Resultado de confirmar un canje de puntos ya realizado")
public class CanjeConfirmacionResponseDto {

    @Schema(description = "Puntos descontados de la cuenta del usuario", example = "4520")
    private Integer puntosCanjeados;

    @Schema(description = "Monto en pesos a aplicar como descuento", example = "452")
    private Integer montoDescuento;

    @Schema(description = "Puntos restantes después del canje", example = "3")
    private Integer puntosRestantes;
}
