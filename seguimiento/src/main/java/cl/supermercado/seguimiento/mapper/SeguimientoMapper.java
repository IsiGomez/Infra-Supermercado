package cl.supermercado.seguimiento.mapper;

import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;
import cl.supermercado.seguimiento.model.Seguimiento;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SeguimientoMapper {

    public Seguimiento toEntity(SeguimientoRequestDto dto) {
        Seguimiento s = new Seguimiento();
        s.setCompraId(dto.getCompraId());
        s.setUsuarioId(dto.getUsuarioId());
        s.setEstado(dto.getEstado());
        s.setFechaActualizacion(LocalDateTime.now());
        return s;
    }

    public SeguimientoResponseDto toDto(Seguimiento s) {
        return new SeguimientoResponseDto(
                s.getId(),
                s.getCompraId(),
                s.getUsuarioId(),
                s.getEstado(),
                s.getFechaActualizacion()
        );
    }

}
