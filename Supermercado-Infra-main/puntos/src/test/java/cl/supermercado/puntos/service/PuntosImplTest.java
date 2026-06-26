package cl.supermercado.puntos.service;

import cl.supermercado.puntos.dto.request.PuntosRequestDto;
import cl.supermercado.puntos.dto.response.CanjeConfirmacionResponseDto;
import cl.supermercado.puntos.dto.response.CanjeSimulacionResponseDto;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;
import cl.supermercado.puntos.mapper.PuntosMapper;
import cl.supermercado.puntos.model.Puntos;
import cl.supermercado.puntos.model.PuntosHistorial;
import cl.supermercado.puntos.repository.PuntosHistorialRepository;
import cl.supermercado.puntos.repository.PuntosRepository;
import cl.supermercado.puntos.service.impl.PuntosServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - PuntosImpl")
public class PuntosImplTest {

    @Mock
    private PuntosRepository repository;

    @Mock
    private PuntosHistorialRepository historialRepository;

    private PuntosServiceImpl puntosService;

    private final Long usuarioId = 1L;

    @BeforeEach
    void setUp() {
        PuntosMapper mapper = new PuntosMapper();
        puntosService = new PuntosServiceImpl(repository, historialRepository, mapper);
    }


    @Test
    @DisplayName("asignarPuntos: debería otorgar 1 punto por cada $100 de la compra, a un usuario nuevo")
    void asignarPuntos_deberiaOtorgarPuntosCorrectos_cuandoUsuarioEsNuevo() {
        PuntosRequestDto request = new PuntosRequestDto(usuarioId, 50L, 15000.0); // 15000 / 100 = 150 puntos
        when(historialRepository.existsByCompraId(50L)).thenReturn(false);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());

        when(repository.save(any(Puntos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PuntosResponseDto result = puntosService.asignarPuntos(request);

        assertThat(result.getPuntosAcumulados()).isEqualTo(150);
    }


    @Test
    @DisplayName("asignarPuntos: debería sumar los puntos nuevos a los que ya tenía el usuario")
    void asignarPuntos_deberiaSumarPuntos_cuandoUsuarioYaTenia() {
        PuntosRequestDto request = new PuntosRequestDto(usuarioId, 51L, 10000.0); // suma 100 puntos
        Puntos existente = new Puntos(1L, usuarioId, 200); // ya tenía 200

        when(historialRepository.existsByCompraId(51L)).thenReturn(false);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(existente));
        when(repository.save(any(Puntos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PuntosResponseDto result = puntosService.asignarPuntos(request);

        assertThat(result.getPuntosAcumulados()).isEqualTo(300);
    }


    @Test
    @DisplayName("asignarPuntos: debería registrar el historial como ACUMULACION con puntos positivos")
    void asignarPuntos_deberiaRegistrarHistorialComoAcumulacion() {
        PuntosRequestDto request = new PuntosRequestDto(usuarioId, 52L, 10000.0);
        when(historialRepository.existsByCompraId(52L)).thenReturn(false);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());
        when(repository.save(any(Puntos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<PuntosHistorial> captor = ArgumentCaptor.forClass(PuntosHistorial.class);

        puntosService.asignarPuntos(request);

        verify(historialRepository).save(captor.capture());
        PuntosHistorial historial = captor.getValue();
        assertThat(historial.getTipo()).isEqualTo("ACUMULACION");
        assertThat(historial.getPuntosOtorgados()).isEqualTo(100);
        assertThat(historial.getCompraId()).isEqualTo(52L);
    }


    @Test
    @DisplayName("asignarPuntos: debería lanzar excepción cuando ya se asignaron puntos por esa compra")
    void asignarPuntos_deberiaLanzarExcepcion_cuandoCompraYaTienePuntosAsignados() {
        PuntosRequestDto request = new PuntosRequestDto(usuarioId, 50L, 15000.0);
        when(historialRepository.existsByCompraId(50L)).thenReturn(true);

        assertThatThrownBy(() -> puntosService.asignarPuntos(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya se asignaron puntos");

        verify(repository, never()).findByUsuarioId(any());
        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("consultarPuntos: debería lanzar excepción cuando el usuario no tiene puntos registrados")
    void consultarPuntos_deberiaLanzarExcepcion_cuandoUsuarioNoTienePuntos() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> puntosService.consultarPuntos(usuarioId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario sin puntos registrados");
    }


    @Test
    @DisplayName("simularCanje: debería redondear hacia abajo al múltiplo de 10 (4523 -> 4520 puntos, $452)")
    void simularCanje_deberiaRedondearHaciaAbajo_cuandoPuntosNoSonMultiploDe10() {
        Puntos puntos = new Puntos(1L, usuarioId, 4523);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(puntos));

        CanjeSimulacionResponseDto result = puntosService.simularCanje(usuarioId);

        assertThat(result.getPuntosDisponibles()).isEqualTo(4523);
        assertThat(result.getPuntosCanjeables()).isEqualTo(4520);
        assertThat(result.getMontoDescuento()).isEqualTo(452);
        assertThat(result.isPuedeCanjear()).isTrue();
    }


    @Test
    @DisplayName("simularCanje: NO debería permitir canjear cuando hay menos de 10 puntos (ej. 9)")
    void simularCanje_noDeberiaPermitirCanjear_cuandoHayMenosDe10Puntos() {
        Puntos puntos = new Puntos(1L, usuarioId, 9);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(puntos));

        CanjeSimulacionResponseDto result = puntosService.simularCanje(usuarioId);

        assertThat(result.getPuntosCanjeables()).isZero();
        assertThat(result.getMontoDescuento()).isZero();
        assertThat(result.isPuedeCanjear()).isFalse();
    }


    @Test
    @DisplayName("simularCanje: debería retornar 0 puntos disponibles cuando el usuario no tiene registro de puntos")
    void simularCanje_deberiaRetornarCero_cuandoUsuarioNoTienePuntosRegistrados() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());

        CanjeSimulacionResponseDto result = puntosService.simularCanje(usuarioId);

        assertThat(result.getPuntosDisponibles()).isZero();
        assertThat(result.isPuedeCanjear()).isFalse();
    }


    @Test
    @DisplayName("confirmarCanje: debería descontar los puntos canjeados y dejar el resto (residuo) intacto")
    void confirmarCanje_deberiaDescontarPuntosCanjeadosYDejarResiduo() {
        Puntos puntos = new Puntos(1L, usuarioId, 4523);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(puntos));
        when(repository.save(any(Puntos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CanjeConfirmacionResponseDto result = puntosService.confirmarCanje(usuarioId);

        assertThat(result.getPuntosCanjeados()).isEqualTo(4520);
        assertThat(result.getMontoDescuento()).isEqualTo(452);
        assertThat(result.getPuntosRestantes()).isEqualTo(3);
        assertThat(puntos.getPuntosAcumulados()).isEqualTo(3);
    }


    @Test
    @DisplayName("confirmarCanje: debería registrar el historial como CANJE con puntos negativos y sin compraId")
    void confirmarCanje_deberiaRegistrarHistorialComoCanje() {
        Puntos puntos = new Puntos(1L, usuarioId, 50);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(puntos));
        when(repository.save(any(Puntos.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<PuntosHistorial> captor = ArgumentCaptor.forClass(PuntosHistorial.class);

        puntosService.confirmarCanje(usuarioId);

        verify(historialRepository).save(captor.capture());
        PuntosHistorial historial = captor.getValue();
        assertThat(historial.getTipo()).isEqualTo("CANJE");
        assertThat(historial.getPuntosOtorgados()).isEqualTo(-50);
        assertThat(historial.getCompraId()).isNull();
    }


    @Test
    @DisplayName("confirmarCanje: debería lanzar excepción cuando el usuario no tiene suficientes puntos para canjear")
    void confirmarCanje_deberiaLanzarExcepcion_cuandoNoHaySuficientesPuntos() {
        Puntos puntos = new Puntos(1L, usuarioId, 5);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(puntos));

        assertThatThrownBy(() -> puntosService.confirmarCanje(usuarioId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No tienes suficientes puntos");

        verify(repository, never()).save(any());
        verify(historialRepository, never()).save(any());
    }


    @Test
    @DisplayName("confirmarCanje: debería lanzar excepción cuando el usuario no tiene puntos registrados")
    void confirmarCanje_deberiaLanzarExcepcion_cuandoUsuarioNoTienePuntosRegistrados() {
        when(repository.findByUsuarioId(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> puntosService.confirmarCanje(usuarioId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario sin puntos registrados");
    }

}
