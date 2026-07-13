package cl.supermercado.seguimiento.service;

import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;
import cl.supermercado.seguimiento.mapper.SeguimientoMapper;
import cl.supermercado.seguimiento.model.Seguimiento;
import cl.supermercado.seguimiento.repository.SeguimientoRepository;
import cl.supermercado.seguimiento.service.impl.SeguimientoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - SeguimientoServiceImpl")
public class SeguimientoImplTest {

    @Mock
    private SeguimientoRepository repository;

    private SeguimientoServiceImpl seguimientoService;

    private final Long compraId  = 1L;
    private final Long usuarioId = 2L;

    @BeforeEach
    void setUp() {
        SeguimientoMapper mapper = new SeguimientoMapper();
        seguimientoService = new SeguimientoServiceImpl(repository, mapper);
    }


    @Test
    @DisplayName("registrarSeguimiento: debería guardar y retornar el seguimiento correctamente")
    void registrarSeguimiento_deberiaGuardarYRetornar_cuandoRequestEsValido() {
        SeguimientoRequestDto request = new SeguimientoRequestDto(compraId, usuarioId, "PENDIENTE");

        Seguimiento entidadGuardada = new Seguimiento(1L, compraId, usuarioId, "PENDIENTE", LocalDateTime.now());
        when(repository.save(any(Seguimiento.class))).thenReturn(entidadGuardada);

        SeguimientoResponseDto result = seguimientoService.registrarSeguimiento(request);

        assertThat(result).isNotNull();
        assertThat(result.getCompraId()).isEqualTo(compraId);
        assertThat(result.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        assertThat(result.getFechaActualizacion()).isNotNull();
        verify(repository, times(1)).save(any(Seguimiento.class));
    }


    @Test
    @DisplayName("registrarSeguimiento: debería registrar el estado ENVIADO correctamente")
    void registrarSeguimiento_deberiaRegistrarEstadoEnviado() {
        SeguimientoRequestDto request = new SeguimientoRequestDto(compraId, usuarioId, "ENVIADO");

        Seguimiento entidadGuardada = new Seguimiento(2L, compraId, usuarioId, "ENVIADO", LocalDateTime.now());
        when(repository.save(any(Seguimiento.class))).thenReturn(entidadGuardada);

        SeguimientoResponseDto result = seguimientoService.registrarSeguimiento(request);

        assertThat(result.getEstado()).isEqualTo("ENVIADO");
        verify(repository, times(1)).save(any(Seguimiento.class));
    }


    @Test
    @DisplayName("listarSeguimientos: debería retornar todos los seguimientos registrados")
    void listarSeguimientos_deberiaRetornarTodos_cuandoExistenRegistros() {
        Seguimiento s1 = new Seguimiento(1L, compraId, usuarioId, "PENDIENTE", LocalDateTime.now());
        Seguimiento s2 = new Seguimiento(2L, compraId, usuarioId, "ENVIADO", LocalDateTime.now());
        when(repository.findAll()).thenReturn(List.of(s1, s2));

        List<SeguimientoResponseDto> result = seguimientoService.listarSeguimientos();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEstado()).isEqualTo("PENDIENTE");
        assertThat(result.get(1).getEstado()).isEqualTo("ENVIADO");
        verify(repository, times(1)).findAll();
    }


    @Test
    @DisplayName("listarSeguimientos: debería retornar lista vacía cuando no hay registros")
    void listarSeguimientos_deberiaRetornarListaVacia_cuandoNoHayRegistros() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<SeguimientoResponseDto> result = seguimientoService.listarSeguimientos();

        assertThat(result).isEmpty();
        verify(repository, times(1)).findAll();
    }


    @Test
    @DisplayName("historialPorCompra: debería retornar el historial de estados de una compra")
    void historialPorCompra_deberiaRetornarHistorial_cuandoExistenRegistros() {
        Seguimiento s1 = new Seguimiento(1L, compraId, usuarioId, "PENDIENTE",   LocalDateTime.now().minusHours(2));
        Seguimiento s2 = new Seguimiento(2L, compraId, usuarioId, "PREPARACION", LocalDateTime.now().minusHours(1));
        Seguimiento s3 = new Seguimiento(3L, compraId, usuarioId, "ENVIADO",     LocalDateTime.now());
        when(repository.findByCompraId(compraId)).thenReturn(List.of(s1, s2, s3));

        List<SeguimientoResponseDto> result = seguimientoService.historialPorCompra(compraId);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getEstado()).isEqualTo("PENDIENTE");
        assertThat(result.get(1).getEstado()).isEqualTo("PREPARACION");
        assertThat(result.get(2).getEstado()).isEqualTo("ENVIADO");
        verify(repository, times(1)).findByCompraId(compraId);
    }


    @Test
    @DisplayName("historialPorCompra: debería retornar lista vacía cuando la compra no tiene seguimientos")
    void historialPorCompra_deberiaRetornarListaVacia_cuandoCompraNoTieneSeguimientos() {
        when(repository.findByCompraId(compraId)).thenReturn(Collections.emptyList());

        List<SeguimientoResponseDto> result = seguimientoService.historialPorCompra(compraId);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByCompraId(compraId);
    }


    @Test
    @DisplayName("listarPorUsuario: debería retornar todos los seguimientos del usuario")
    void listarPorUsuario_deberiaRetornarSeguimientos_cuandoUsuarioTieneCompras() {
        Seguimiento s1 = new Seguimiento(1L, 10L, usuarioId, "ENTREGADO", LocalDateTime.now().minusDays(1));
        Seguimiento s2 = new Seguimiento(2L, 11L, usuarioId, "ENVIADO",   LocalDateTime.now());
        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of(s1, s2));

        List<SeguimientoResponseDto> result = seguimientoService.listarPorUsuario(usuarioId);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(s -> s.getUsuarioId().equals(usuarioId));
        verify(repository, times(1)).findByUsuarioId(usuarioId);
    }


    @Test
    @DisplayName("listarPorUsuario: debería retornar lista vacía cuando el usuario no tiene compras")
    void listarPorUsuario_deberiaRetornarListaVacia_cuandoUsuarioNoTieneCompras() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Collections.emptyList());

        List<SeguimientoResponseDto> result = seguimientoService.listarPorUsuario(usuarioId);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByUsuarioId(usuarioId);
    }


    @Test
    @DisplayName("obtenerPorId: debería retornar el seguimiento correctamente cuando existe")
    void obtenerPorId_deberiaRetornarSeguimiento_cuandoExiste() {
        Seguimiento seguimiento = new Seguimiento(1L, compraId, usuarioId, "PENDIENTE", LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(seguimiento));

        SeguimientoResponseDto result = seguimientoService.obtenerPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEstado()).isEqualTo("PENDIENTE");
        verify(repository, times(1)).findById(1L);
    }


    @Test
    @DisplayName("obtenerPorId: debería lanzar EntityNotFoundException cuando el seguimiento no existe")
    void obtenerPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> seguimientoService.obtenerPorId(99L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("Seguimiento no encontrado con id: 99");

        verify(repository, times(1)).findById(99L);
    }

}
