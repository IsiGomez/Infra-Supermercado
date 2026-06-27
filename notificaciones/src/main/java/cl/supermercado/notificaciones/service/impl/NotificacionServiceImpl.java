package cl.supermercado.notificaciones.service.impl;

import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.mapper.NotificacionMapper;
import cl.supermercado.notificaciones.model.Notificacion;
import cl.supermercado.notificaciones.repository.NotificacionRepository;
import cl.supermercado.notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository repository;
    private final NotificacionMapper mapper;


    @Override
    @Transactional
    public NotificacionResponseDto enviarNotificacion(NotificacionRequestDto request) {
        log.info("Guardando notificación para usuarioId={}", request.getUsuarioId());
        Notificacion n = mapper.toEntity(request);

        repository.save(n);
        return mapper.toDto(n);
    }


    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponseDto> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(mapper::toDto).toList();
    }


    @Override
    @Transactional
    public NotificacionResponseDto marcarComoLeida(Long id) {
        Notificacion n = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        n.setLeido(true);
        repository.save(n);
        return mapper.toDto(n);
    }
}
