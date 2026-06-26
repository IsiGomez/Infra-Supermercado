package cl.duoc.carrito.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CanjeConfirmacionDto", description = "DTO de comunicacion con microservicio puntos")
public class CanjeConfirmacionDto {

    @Schema(description = "Puntos a canjear")
    private Integer puntosCanjeados;

    @Schema(description = "Monto de descuento")
    private Integer montoDescuento;

    @Schema(description = "Puntos sobrantes del canjeo")
    private Integer puntosRestantes;

}
