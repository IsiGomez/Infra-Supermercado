package cl.supermercado.puntos.mapper;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;
import cl.supermercado.puntos.model.Puntos;

import org.springframework.stereotype.Component;
@Component
public class PuntosMapper {

    public PuntosResponseDto toDto(Puntos p) {
        return new PuntosResponseDto(p.getUsuarioId(), p.getPuntosAcumulados());
    }

}