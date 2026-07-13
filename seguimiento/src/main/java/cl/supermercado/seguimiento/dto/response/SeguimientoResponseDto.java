package cl.supermercado.seguimiento.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "SeguimientoResponseDto", description = "DTO en respuesta del seguimiento")
public class SeguimientoResponseDto {

    @Schema(description = "ID del seguimiento", example = "3")
    private Long id;

    @Schema(description = "ID de la compra perteneciente al seguimiento", example = "15")
    private Long compraId;

    @Schema(description = "ID del usuario perteneciento", example = "2")
    private Long usuarioId;

    @Schema(description = "Estado de la compra", example = "PREPARACION")
    private String estado;

    @Schema(description = "Fecha en la que se actualizo", example = "2026-07-11T14:30:00")
    private LocalDateTime fechaActualizacion;

}
