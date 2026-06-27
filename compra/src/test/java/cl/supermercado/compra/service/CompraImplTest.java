package cl.supermercado.compra.service;

import cl.supermercado.compra.dto.remote.CartDto;
import cl.supermercado.compra.dto.remote.CartItemDto;
import cl.supermercado.compra.dto.request.CompraRequestDto;
import cl.supermercado.compra.dto.response.CompraResponseDto;
import cl.supermercado.compra.event.CompraCompletadaEvent;
import cl.supermercado.compra.mapper.CompraMapper;
import cl.supermercado.compra.model.Compra;
import cl.supermercado.compra.repository.CompraRepository;
import cl.supermercado.compra.service.api.CarritoClient;
import cl.supermercado.compra.service.api.PagoClient;
import cl.supermercado.compra.service.impl.CompraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - CompraImpl")
public class CompraImplTest {

    @Mock
    private CompraRepository repository;

    @Mock
    private CarritoClient carritoClient;

    @Mock
    private PagoClient pagoClient;

    @Mock
    private KafkaTemplate<String, CompraCompletadaEvent> kafkaTemplate;

    private CompraServiceImpl compraService;

    private final Long usuarioId = 1L;
    private CartDto carritoConItems;

    @BeforeEach
    void setUp() {
        CompraMapper mapper = new CompraMapper();
        compraService = new CompraServiceImpl(repository, mapper, carritoClient, pagoClient, kafkaTemplate);

        CartItemDto item = new CartItemDto(1L, 10L, "Arroz 1kg", 2, 3000);
        carritoConItems = new CartDto(1L, usuarioId, List.of(item), 3000);
    }


    @Test
    @DisplayName("crearCompra: debería lanzar excepción cuando el usuario no tiene un pago exitoso")
    void crearCompra_deberiaLanzarExcepcion_cuandoNoHayPagoExitoso() {
        CompraRequestDto request = new CompraRequestDto(usuarioId);
        when(pagoClient.tieneUltimoPagoExitoso(usuarioId)).thenReturn(false);

        assertThatThrownBy(() -> compraService.crearCompra(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no tiene un pago exitoso");

        verify(carritoClient, never()).obtenerCarrito(anyLong());
        verify(repository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(String.class), any(), any());
    }


    @Test
    @DisplayName("crearCompra: debería lanzar excepción cuando el carrito está vacío")
    void crearCompra_deberiaLanzarExcepcion_cuandoCarritoEstaVacio() {
        CompraRequestDto request = new CompraRequestDto(usuarioId);
        CartDto carritoVacio = new CartDto(1L, usuarioId, Collections.emptyList(), 0);

        when(pagoClient.tieneUltimoPagoExitoso(usuarioId)).thenReturn(true);
        when(carritoClient.obtenerCarrito(usuarioId)).thenReturn(carritoVacio);

        assertThatThrownBy(() -> compraService.crearCompra(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carrito está vacío");

        verify(repository, never()).save(any());
        verify(carritoClient, never()).limpiarCarrito(anyLong());
        verify(kafkaTemplate, never()).send(any(String.class), any(), any());
    }


    @Test
    @DisplayName("crearCompra: debería lanzar excepción cuando el carrito devuelto es null")
    void crearCompra_deberiaLanzarExcepcion_cuandoCarritoEsNull() {
        CompraRequestDto request = new CompraRequestDto(usuarioId);

        when(pagoClient.tieneUltimoPagoExitoso(usuarioId)).thenReturn(true);
        when(carritoClient.obtenerCarrito(usuarioId)).thenReturn(null);

        assertThatThrownBy(() -> compraService.crearCompra(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carrito está vacío");
    }


    @Test
    @DisplayName("crearCompra: debería crear la compra, limpiar el carrito y publicar el evento Kafka")
    void crearCompra_deberiaCrearCompra_cuandoPagoYCarritoSonValidos() {
        CompraRequestDto request = new CompraRequestDto(usuarioId);

        when(pagoClient.tieneUltimoPagoExitoso(usuarioId)).thenReturn(true);
        when(carritoClient.obtenerCarrito(usuarioId)).thenReturn(carritoConItems);
        when(repository.save(any(Compra.class))).thenAnswer(invocation -> {
            Compra c = invocation.getArgument(0);
            c.setId(100L); // simula el id autogenerado al guardar
            return c;
        });

        CompraResponseDto result = compraService.crearCompra(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(result.getTotal()).isEqualTo(3000.0);
        assertThat(result.getFinalizada()).isTrue();
        assertThat(result.getPagoConfirmado()).isTrue();

        verify(carritoClient, times(1)).limpiarCarrito(usuarioId);

        ArgumentCaptor<CompraCompletadaEvent> eventoCaptor = ArgumentCaptor.forClass(CompraCompletadaEvent.class);
        verify(kafkaTemplate, times(1))
                .send(eq("compra-completada"), eq(String.valueOf(usuarioId)), eventoCaptor.capture());

        CompraCompletadaEvent eventoEnviado = eventoCaptor.getValue();
        assertThat(eventoEnviado.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(eventoEnviado.getTotal()).isEqualTo(3000.0);
        assertThat(eventoEnviado.getItems()).hasSize(1);
        assertThat(eventoEnviado.getItems().get(0).getProductId()).isEqualTo(10L);
        assertThat(eventoEnviado.getItems().get(0).getQuantity()).isEqualTo(2);
    }


    @Test
    @DisplayName("listarComprasPorUsuario: debería retornar solo las compras del usuario solicitado")
    void listarComprasPorUsuario_deberiaRetornarComprasDelUsuario() {
        Compra compra = new Compra(1L, usuarioId, 3000.0,
                java.time.LocalDateTime.now(), true, true);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of(compra));

        List<CompraResponseDto> result = compraService.listarComprasPorUsuario(usuarioId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsuarioId()).isEqualTo(usuarioId);
    }

}
