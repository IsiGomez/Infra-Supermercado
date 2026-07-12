package cl.supermercado.notificaciones.service;

import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;

import java.util.List;

public interface NotificacionService {

    NotificacionResponseDto enviarNotificacion(NotificacionRequestDto request);

    List<NotificacionResponseDto> listarPorUsuario(Long usuarioId);
    NotificacionResponseDto marcarComoLeida(Long id);
    NotificacionResponseDto obtenerPorId(Long id);

}
