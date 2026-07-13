package cl.supermercado.puntos.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PuntosResponseDto", description = "DTO de respuesta de puntos acumulados")
public class PuntosResponseDto {

    @Schema(description = "Id del usuario", example = "2")
    private Long usuarioId;

    @Schema(description = "Cantidad de puntos acumulados", example = "150")
    private Integer puntosAcumulados;

}