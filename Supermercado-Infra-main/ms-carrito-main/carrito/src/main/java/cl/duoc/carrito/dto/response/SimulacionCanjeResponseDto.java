package cl.duoc.carrito.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "SimulacionCanjeResponseDto", description = "DTO de respuesta de la simulacion de canjeo de puntos")
public class SimulacionCanjeResponseDto {

    @Schema(description = "Cantidad de puntos disponible")
    private Integer puntosDisponibles;

    @Schema(description = "Puntos cajeables")
    private Integer puntosCanjeables;

    @Schema(description = "Monto a descontar")
    private Integer montoDescuento;

    @Schema(description = "Si es que se puede canjear")
    private boolean puedeCanjear;

    @Schema(description = "Total del carrito")
    private Integer totalCarritoActual;

    @Schema(description = "Total final del carrito en caso de canjear")
    private Integer totalCarritoConDescuento;

    @Schema(description = "Mensaje")
    private String mensaje;
}
