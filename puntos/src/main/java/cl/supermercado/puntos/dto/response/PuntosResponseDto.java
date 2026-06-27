package cl.supermercado.puntos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class PuntosResponseDto {

    private Long usuarioId;
    private Integer puntosAcumulados;

}