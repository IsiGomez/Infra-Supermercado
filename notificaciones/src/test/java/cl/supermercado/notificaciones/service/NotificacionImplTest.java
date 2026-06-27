package cl.supermercado.notificaciones.service;

import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.dto.response.NotificacionResponseDto;
import cl.supermercado.notificaciones.mapper.NotificacionMapper;
import cl.supermercado.notificaciones.model.Notificacion;
import cl.supermercado.notificaciones.repository.NotificacionRepository;
import cl.supermercado.notificaciones.service.impl.NotificacionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - NotificacionServiceImpl")
public class NotificacionImplTest {

    @Mock
    private NotificacionRepository repository;

    private NotificacionServiceImpl notificacionService;

    private final Long usuarioId = 2L;

    @BeforeEach
    void setUp() {
        NotificacionMapper mapper = new NotificacionMapper();
        notificacionService = new NotificacionServiceImpl(repository, mapper);
    }


    @Test
    @DisplayName("enviarNotificacion: debería guardar y retornar la notificación correctamente")
    void enviarNotificacion_deberiaGuardarYRetornar_cuandoRequestEsValido() {
        NotificacionRequestDto request = new NotificacionRequestDto(usuarioId, "Tu compra #15 ha sido enviada");

        Notificacion guardada = new Notificacion(1L, usuarioId, "Tu compra #15 ha sido enviada",
                LocalDateTime.now(), true, false);
        when(repository.save(any(Notificacion.class))).thenReturn(guardada);

        NotificacionResponseDto result = notificacionService.enviarNotificacion(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(result.getMensaje()).isEqualTo("Tu compra #15 ha sido enviada");
        assertThat(result.getEnviado()).isTrue();
        assertThat(result.getLeido()).isFalse();
        assertThat(result.getFechaEnvio()).isNotNull();
        verify(repository, times(1)).save(any(Notificacion.class));
    }


    @Test
    @DisplayName("enviarNotificacion: la notificación debe crearse con leido en false por defecto")
    void enviarNotificacion_deberiaCrarseConLeidoFalse_porDefecto() {
        NotificacionRequestDto request = new NotificacionRequestDto(usuarioId, "Bienvenido al sistema");

        Notificacion guardada = new Notificacion(2L, usuarioId, "Bienvenido al sistema",
                LocalDateTime.now(), true, false);
        when(repository.save(any(Notificacion.class))).thenReturn(guardada);

        NotificacionResponseDto result = notificacionService.enviarNotificacion(request);

        assertThat(result.getLeido()).isFalse();
        assertThat(result.getEnviado()).isTrue();
        verify(repository, times(1)).save(any(Notificacion.class));
    }


    @Test
    @DisplayName("listarPorUsuario: debería retornar todas las notificaciones del usuario")
    void listarPorUsuario_deberiaRetornarNotificaciones_cuandoExistenRegistros() {
        Notificacion n1 = new Notificacion(1L, usuarioId, "Compra #10 enviada",   LocalDateTime.now().minusDays(1), true, true);
        Notificacion n2 = new Notificacion(2L, usuarioId, "Compra #15 en camino", LocalDateTime.now(),              true, false);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of(n1, n2));

        List<NotificacionResponseDto> result = notificacionService.listarPorUsuario(usuarioId);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(n -> n.getUsuarioId().equals(usuarioId));
        assertThat(result.get(0).getLeido()).isTrue();
        assertThat(result.get(1).getLeido()).isFalse();
        verify(repository, times(1)).findByUsuarioId(usuarioId);
    }


    @Test
    @DisplayName("listarPorUsuario: debería retornar lista vacía cuando el usuario no tiene notificaciones")
    void listarPorUsuario_deberiaRetornarListaVacia_cuandoNoHayNotificaciones() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Collections.emptyList());

        List<NotificacionResponseDto> result = notificacionService.listarPorUsuario(usuarioId);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByUsuarioId(usuarioId);
    }


    @Test
    @DisplayName("marcarComoLeida: debería actualizar leido a true cuando la notificación existe")
    void marcarComoLeida_deberiaActualizarLeido_cuandoNotificacionExiste() {
        Notificacion existente = new Notificacion(1L, usuarioId, "Compra #10 enviada",
                LocalDateTime.now(), true, false);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        NotificacionResponseDto result = notificacionService.marcarComoLeida(1L);

        assertThat(result.getLeido()).isTrue();
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existente);
    }


    @Test
    @DisplayName("marcarComoLeida: debería lanzar excepción cuando la notificación no existe")
    void marcarComoLeida_deberiaLanzarExcepcion_cuandoNotificacionNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificacionService.marcarComoLeida(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notificación no encontrada");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("marcarComoLeida: no debería modificar otros campos al marcar como leída")
    void marcarComoLeida_noDeberiaModificarOtrosCampos_alMarcarComoLeida() {
        LocalDateTime fechaOriginal = LocalDateTime.now().minusHours(3);
        Notificacion existente = new Notificacion(1L, usuarioId, "Mensaje original",
                fechaOriginal, true, false);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        NotificacionResponseDto result = notificacionService.marcarComoLeida(1L);

        assertThat(result.getLeido()).isTrue();
        assertThat(result.getMensaje()).isEqualTo("Mensaje original");
        assertThat(result.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(result.getFechaEnvio()).isEqualTo(fechaOriginal);
        assertThat(result.getEnviado()).isTrue();
    }

}
