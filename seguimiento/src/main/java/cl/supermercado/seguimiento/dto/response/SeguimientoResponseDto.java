package cl.supermercado.seguimiento.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class SeguimientoResponseDto {

    private Long id;
    private Long compraId;
    private Long usuarioId;
    private String estado;
    private LocalDateTime fechaActualizacion;

}
