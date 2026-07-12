package cl.supermercado.pago.service;

import cl.supermercado.pago.dto.request.PagoRequestDto;
import cl.supermercado.pago.dto.response.PagoResponseDto;
import cl.supermercado.pago.mapper.PagoMapper;
import cl.supermercado.pago.model.Pago;
import cl.supermercado.pago.repository.PagoRepository;
import cl.supermercado.pago.service.api.CarritoClient;
import cl.supermercado.pago.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - PagoImpl")
public class PagoImplTest {

    @Mock
    private PagoRepository repository;

    @Mock
    private CarritoClient carritoClient;

    private PagoServiceImpl pagoService;

    private final Long usuarioId = 1L;

    @BeforeEach
    void setUp() {
        PagoMapper mapper = new PagoMapper();
        pagoService = new PagoServiceImpl(repository, mapper, carritoClient);
    }


    @Test
    @DisplayName("tieneUltimoPagoExitoso: debería retornar true cuando existe un pago exitoso previo")
    void tieneUltimoPagoExitoso_deberiaRetornarTrue_cuandoExistePagoExitoso() {
        Pago pagoExitoso = new Pago(1L, usuarioId, 3000.0, "TARJETA", true, LocalDateTime.now());
        when(repository.findTopByUsuarioIdAndExitosoTrueOrderByFechaPagoDesc(usuarioId))
                .thenReturn(Optional.of(pagoExitoso));

        Boolean result = pagoService.tieneUltimoPagoExitoso(usuarioId);

        assertThat(result).isTrue();
    }


    @Test
    @DisplayName("tieneUltimoPagoExitoso: debería retornar false cuando no hay pagos exitosos previos")
    void tieneUltimoPagoExitoso_deberiaRetornarFalse_cuandoNoHayPagoExitoso() {
        when(repository.findTopByUsuarioIdAndExitosoTrueOrderByFechaPagoDesc(usuarioId))
                .thenReturn(Optional.empty());

        Boolean result = pagoService.tieneUltimoPagoExitoso(usuarioId);

        assertThat(result).isFalse();
    }


    @Test
    @DisplayName("obtenerUltimoPagoExitoso: debería lanzar excepción cuando no existe pago exitoso")
    void obtenerUltimoPagoExitoso_deberiaLanzarExcepcion_cuandoNoExistePago() {
        when(repository.findTopByUsuarioIdAndExitosoTrueOrderByFechaPagoDesc(usuarioId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerUltimoPagoExitoso(usuarioId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe un pago exitoso");
    }


    @Test
    @DisplayName("procesarPago: debería procesar el pago exitosamente cuando el método es TARJETA")
    void procesarPago_deberiaProcesarPago_cuandoMetodoEsTarjeta() {
        PagoRequestDto request = new PagoRequestDto(usuarioId, "TARJETA");

        when(carritoClient.obtenerTotalCarrito(usuarioId)).thenReturn(3000.0);
        when(repository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagoResponseDto result = pagoService.procesarPago(request);

        assertThat(result).isNotNull();
        assertThat(result.getExitoso()).isTrue();
        assertThat(result.getMonto()).isEqualTo(3000.0);
        assertThat(result.getMetodo()).isEqualTo("TARJETA");
    }


    @Test
    @DisplayName("procesarPago: debería normalizar el método de pago a mayúsculas (ej. 'efectivo' -> 'EFECTIVO')")
    void procesarPago_deberiaNormalizarMetodoAMayusculas() {
        PagoRequestDto request = new PagoRequestDto(usuarioId, "efectivo");
        when(carritoClient.obtenerTotalCarrito(usuarioId)).thenReturn(1500.0);

        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);
        when(repository.save(pagoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        pagoService.procesarPago(request);

        assertThat(pagoCaptor.getValue().getMetodo()).isEqualTo("EFECTIVO");
    }


    @Test
    @DisplayName("procesarPago: debería lanzar excepción cuando el método de pago es inválido")
    void procesarPago_deberiaLanzarExcepcion_cuandoMetodoEsInvalido() {
        PagoRequestDto request = new PagoRequestDto(usuarioId, "CRIPTOMONEDA");
        when(carritoClient.obtenerTotalCarrito(usuarioId)).thenReturn(2000.0);

        assertThatThrownBy(() -> pagoService.procesarPago(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("procesarPago: debería lanzar excepción cuando el carrito del usuario está vacío")
    void procesarPago_deberiaLanzarExcepcion_cuandoCarritoEstaVacio() {
        PagoRequestDto request = new PagoRequestDto(usuarioId, "TARJETA");
        when(carritoClient.obtenerTotalCarrito(usuarioId)).thenReturn(0.0);

        assertThatThrownBy(() -> pagoService.procesarPago(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carrito del usuario está vacío");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("procesarPago: debería lanzar excepción cuando no se puede contactar al microservicio carrito")
    void procesarPago_deberiaLanzarExcepcion_cuandoFallaComunicacionConCarrito() {
        PagoRequestDto request = new PagoRequestDto(usuarioId, "TARJETA");
        when(carritoClient.obtenerTotalCarrito(usuarioId))
                .thenThrow(new RuntimeException("Servicio no disponible"));

        assertThatThrownBy(() -> pagoService.procesarPago(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se pudo obtener el carrito");

        verify(repository, never()).save(any());
    }


    @Test
    @DisplayName("obtenerPorId: debería retornar el pago cuando existe")
    void obtenerPorId_deberiaRetornarPago_cuandoExiste() {
        Pago pago = new Pago(1L, usuarioId, 3000.0, "TARJETA", true, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(Optional.of(pago));

        PagoResponseDto result = pagoService.obtenerPorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsuarioId()).isEqualTo(usuarioId);
    }


    @Test
    @DisplayName("obtenerPorId: debería lanzar excepción cuando el pago no existe")
    void obtenerPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.obtenerPorId(99L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

}
