package cl.supermercado.puntos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CanjeSimulacionResponse", description = "Resultado de simular un canje de puntos, antes de confirmarlo")
public class CanjeSimulacionResponseDto {

    @Schema(description = "Puntos acumulados actuales del usuario", example = "4523")
    private Integer puntosDisponibles;

    @Schema(description = "Puntos que efectivamente se pueden canjear " +
            "(múltiplo de 10, redondeado hacia abajo)", example = "4520")
    private Integer puntosCanjeables;

    @Schema(description = "Monto en pesos equivalente a los puntos canjeables " +
            "(tasa: 10 puntos = $1)", example = "452")
    private Integer montoDescuento;

    @Schema(description = "Indica si el usuario tiene al menos el mínimo canjeable (10 puntos)")
    private boolean puedeCanjear;

    @Schema(description = "Mensaje descriptivo para mostrar al cliente",
            example = "Tienes 4523 puntos. Puedes canjear 4520 por $452 de descuento.")
    private String mensaje;
}
