package cl.duoc.carrito.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CanjeConfirmacionDto", description = "DTO de comunicacion con microservicio puntos de simulacion de canjeo")
public class CanjeSimulacionDto {

    @Schema(description = "Puntos disponibles")
    private Integer puntosDisponibles;

    @Schema(description = "Puntos que se pueden canjear")
    private Integer puntosCanjeables;

    @Schema(description = "Monto de descuento")
    private Integer montoDescuento;

    @Schema(description = "Puntos que se pueden canjear")
    private boolean puedeCanjear;

    @Schema(description = "Mensaje")
    private String mensaje;

}
