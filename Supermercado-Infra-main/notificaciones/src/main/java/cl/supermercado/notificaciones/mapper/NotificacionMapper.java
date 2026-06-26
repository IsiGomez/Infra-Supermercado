package cl.supermercado.notificaciones.mapper;

import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.model.Notificacion;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificacionMapper {

    public Notificacion toEntity(NotificacionRequestDto dto) {
        Notificacion n = new Notificacion();
        n.setUsuarioId(dto.getUsuarioId());
        n.setMensaje(dto.getMensaje());
        n.setFechaEnvio(LocalDateTime.now());
        n.setEnviado(true);
        n.setLeido(false);
        return n;
    }

    public NotificacionResponseDto toDto(Notificacion n) {
        return new NotificacionResponseDto(
                n.getId(),
                n.getUsuarioId(),
                n.getMensaje(),
                n.getFechaEnvio(),
                n.getEnviado(),
                n.getLeido()
        );
    }

}