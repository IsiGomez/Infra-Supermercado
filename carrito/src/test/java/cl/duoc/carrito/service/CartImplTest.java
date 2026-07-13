package cl.duoc.carrito.service;

import cl.duoc.carrito.dto.remote.*;
import cl.duoc.carrito.dto.request.CartItemRequestDto;
import cl.duoc.carrito.dto.request.CartRequestDto;
import cl.duoc.carrito.dto.response.CartResponseDto;
import cl.duoc.carrito.dto.response.SimulacionCanjeResponseDto;
import cl.duoc.carrito.mapper.CartMapper;
import cl.duoc.carrito.model.Cart;
import cl.duoc.carrito.model.CartItem;
import cl.duoc.carrito.repository.CartItemRepository;
import cl.duoc.carrito.repository.CartRepository;
import cl.duoc.carrito.service.apis.CatalogoClient;
import cl.duoc.carrito.service.apis.InventarioClient;
import cl.duoc.carrito.service.apis.PromocionesClient;
import cl.duoc.carrito.service.apis.PuntosClient;
import cl.duoc.carrito.service.impl.CartImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - CartImpl")
public class CartImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private PromocionesClient promocionesClient;

    @Mock
    private PuntosClient puntosClient;

    private CartImpl cartService;

    private Cart cart;
    private ProductDto product;
    private InventoryDto stock;
    private final Long userId = 1L;
    private final Long productId = 10L;

    @BeforeEach
    void setUp() {
        CartMapper mapper = new CartMapper();
        cartService = new CartImpl(
                cartRepository, cartItemRepository, catalogoClient,
                inventarioClient, promocionesClient, puntosClient, mapper);

        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);
        cart.setTotal(0);

        product = new ProductDto(productId, "Arroz 1kg", "Arroz grado 1", 1500, null);
        stock = new InventoryDto(1L, productId, 20);

        org.springframework.hateoas.CollectionModel<org.springframework.hateoas.EntityModel<ProductDto>> mockCollection =
                org.springframework.hateoas.CollectionModel.of(java.util.List.of(org.springframework.hateoas.EntityModel.of(product)));

        lenient().when(catalogoClient.getProductsByIds(any())).thenReturn(mockCollection);
    }


    @Test
    @DisplayName("getCart: debería crear un carrito nuevo si el usuario no tiene uno")
    void getCart_deberiaCrearCarritoNuevo_cuandoUsuarioNoTieneUno() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponseDto result = cartService.getCart(userId);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getItems()).isEmpty();
        verify(cartRepository, times(1)).save(any(Cart.class));
    }


    @Test
    @DisplayName("addItem: debería agregar el producto cuando hay stock y no estaba en el carrito")
    void addItem_deberiaAgregarProducto_cuandoHayStockYNoExisteEnCarrito() {
        CartRequestDto request = new CartRequestDto(productId, 2);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(catalogoClient.getProductById(productId)).thenReturn(product);
        when(inventarioClient.consultarStock(productId)).thenReturn(stock);
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.empty());

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponseDto result = cartService.addItem(userId, request);

        assertThat(result).isNotNull();
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    @DisplayName("addItem: debería lanzar excepción cuando el producto no existe en el catálogo")
    void addItem_deberiaLanzarExcepcion_cuandoProductoNoExisteEnCatalogo() {
        CartRequestDto request = new CartRequestDto(productId, 2);

        when(catalogoClient.getProductById(productId)).thenReturn(null);

        assertThatThrownBy(() -> cartService.addItem(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no existe en el catálogo");

        verify(inventarioClient, never()).consultarStock(anyLong());
        verify(cartItemRepository, never()).save(any());
    }


    @Test
    @DisplayName("addItem: debería lanzar excepción cuando el stock es insuficiente")
    void addItem_deberiaLanzarExcepcion_cuandoStockEsInsuficiente() {
        CartRequestDto request = new CartRequestDto(productId, 50);
        InventoryDto stockBajo = new InventoryDto(1L, productId, 5);

        when(catalogoClient.getProductById(productId)).thenReturn(product);
        when(inventarioClient.consultarStock(productId)).thenReturn(stockBajo);

        assertThatThrownBy(() -> cartService.addItem(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock insuficiente");

        verify(cartItemRepository, never()).save(any());
        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("addItem: debería lanzar excepción cuando el producto ya está en el carrito")
    void addItem_deberiaLanzarExcepcion_cuandoProductoYaEstaEnCarrito() {
        CartRequestDto request = new CartRequestDto(productId, 1);
        CartItem itemExistente = new CartItem();
        itemExistente.setProductId(productId);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(catalogoClient.getProductById(productId)).thenReturn(product);
        when(inventarioClient.consultarStock(productId)).thenReturn(stock);
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.of(itemExistente));

        assertThatThrownBy(() -> cartService.addItem(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya esta en el carrito");

        verify(cartItemRepository, never()).save(any());
    }


    @Test
    @DisplayName("updateQuantity: debería actualizar la cantidad cuando el ítem existe y hay stock")
    void updateQuantity_deberiaActualizarCantidad_cuandoItemExisteYHayStock() {
        CartItemRequestDto request = new CartItemRequestDto(5);
        CartItem item = new CartItem();
        item.setProductId(productId);
        item.setQuantity(2);
        item.setSubtotal(3000);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.of(item));
        when(inventarioClient.consultarStock(productId)).thenReturn(stock);
        when(catalogoClient.getProductById(productId)).thenReturn(product);

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.updateQuantity(userId, productId, request);

        assertThat(item.getQuantity()).isEqualTo(5);
        verify(cartItemRepository, times(1)).save(item);
    }


    @Test
    @DisplayName("updateQuantity: debería lanzar excepción cuando el producto no está en el carrito")
    void updateQuantity_deberiaLanzarExcepcion_cuandoProductoNoEstaEnCarrito() {
        CartItemRequestDto request = new CartItemRequestDto(3);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.updateQuantity(userId, productId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("no está en el carrito");

        verify(inventarioClient, never()).consultarStock(anyLong());
    }


    @Test
    @DisplayName("removeItem: debería eliminar el producto cuando existe en el carrito")
    void removeItem_deberiaEliminarProducto_cuandoExisteEnCarrito() {
        CartItem item = new CartItem();
        item.setProductId(productId);
        cart.getItems().add(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.removeItem(userId, productId);

        assertThat(cart.getItems()).doesNotContain(item);
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    @DisplayName("removeItem: debería lanzar excepción cuando el producto no está en el carrito")
    void removeItem_deberiaLanzarExcepcion_cuandoProductoNoEstaEnCarrito() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.removeItem(userId, productId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("clearCart: debería vaciar el carrito y poner el total en cero")
    void clearCart_deberiaVaciarCarritoYPonerTotalEnCero() {
        CartItem item = new CartItem();
        item.setProductId(productId);
        cart.getItems().add(item);
        cart.setTotal(1500);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.clearCart(userId);

        assertThat(cart.getItems()).isEmpty();
        assertThat(cart.getTotal()).isZero();
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    @DisplayName("aplicarPromocion: debería aplicar el descuento correctamente cuando la promoción es válida")
    void aplicarPromocion_deberiaAplicarDescuento_cuandoPromocionEsValida() {
        CartItem item = new CartItem();
        item.setProductId(productId);
        cart.getItems().add(item);
        cart.setTotal(10000);

        PromocionDto promo = new PromocionDto();
        promo.setDescuento(20.0); // 20% de descuento
        promo.setFechaInicio(LocalDate.now().minusDays(1));
        promo.setFechaFin(LocalDate.now().plusDays(1));

        when(promocionesClient.obtenerPromocion("PROMO20")).thenReturn(promo);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.aplicarPromocion(userId, "PROMO20");

        assertThat(cart.getTotal()).isEqualTo(8000); // 10000 - 20% = 8000
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    @DisplayName("aplicarPromocion: debería lanzar excepción cuando la promoción es null")
    void aplicarPromocion_deberiaLanzarExcepcion_cuandoPromocionEsNull() {

        when(promocionesClient.obtenerPromocion("INVALIDA")).thenReturn(null);

        assertThatThrownBy(() -> cartService.aplicarPromocion(userId, "INVALIDA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Promoción inválida o expirada");

        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("aplicarPromocion: debería lanzar excepción cuando la promoción está expirada")
    void aplicarPromocion_deberiaLanzarExcepcion_cuandoPromocionEstaExpirada() {
        PromocionDto promoExpirada = new PromocionDto();
        promoExpirada.setDescuento(10.0);
        promoExpirada.setFechaInicio(LocalDate.now().minusDays(10));
        promoExpirada.setFechaFin(LocalDate.now().minusDays(1)); // ya venció

        when(promocionesClient.obtenerPromocion("EXPIRADA")).thenReturn(promoExpirada);

        assertThatThrownBy(() -> cartService.aplicarPromocion(userId, "EXPIRADA"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Promoción inválida o expirada");

        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("aplicarPromocion: debería lanzar excepción cuando el carrito está vacío")
    void aplicarPromocion_deberiaLanzarExcepcion_cuandoCarritoEstaVacio() {
        PromocionDto promo = new PromocionDto();
        promo.setDescuento(15.0);
        promo.setFechaInicio(LocalDate.now().minusDays(1));
        promo.setFechaFin(LocalDate.now().plusDays(1));

        when(promocionesClient.obtenerPromocion("PROMO15")).thenReturn(promo);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart)); // cart sin items

        assertThatThrownBy(() -> cartService.aplicarPromocion(userId, "PROMO15"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carrito está vacío");

        verify(cartRepository, never()).save(any());
    }


    @Test
    @DisplayName("simularCanjePuntos: debería retornar la simulación correctamente cuando el carrito tiene items")
    void simularCanjePuntos_deberiaRetornarSimulacion_cuandoCarritoTieneItems() {
        CartItem item = new CartItem();
        item.setProductId(productId);
        cart.getItems().add(item);
        cart.setTotal(5000);

        CanjeSimulacionDto simulacion = new CanjeSimulacionDto(200, 150, 1500, true, "Puedes canjear 150 puntos");

        when(puntosClient.simularCanje(userId)).thenReturn(simulacion);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        SimulacionCanjeResponseDto result = cartService.simularCanjePuntos(userId);

        assertThat(result).isNotNull();
        assertThat(result.getPuntosDisponibles()).isEqualTo(200);
        assertThat(result.getPuntosCanjeables()).isEqualTo(150);
        assertThat(result.getMontoDescuento()).isEqualTo(1500);
        assertThat(result.getTotalCarritoActual()).isEqualTo(5000);
        assertThat(result.getTotalCarritoConDescuento()).isEqualTo(3500); // 5000 - 1500
        assertThat(result.isPuedeCanjear()).isTrue();
    }


    @Test
    @DisplayName("simularCanjePuntos: debería lanzar excepción cuando el carrito está vacío")
    void simularCanjePuntos_deberiaLanzarExcepcion_cuandoCarritoEstaVacio() {
        CanjeSimulacionDto simulacion = new CanjeSimulacionDto(100, 100, 1000, true, "Ok");

        when(puntosClient.simularCanje(userId)).thenReturn(simulacion);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart)); // sin items

        assertThatThrownBy(() -> cartService.simularCanjePuntos(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carrito está vacío");
    }


    @Test
    @DisplayName("confirmarCanjePuntos: debería aplicar el descuento al total cuando el canje es exitoso")
    void confirmarCanjePuntos_deberiaAplicarDescuento_cuandoCanjeEsExitoso() {
        CartItem item = new CartItem();
        item.setProductId(productId);
        cart.getItems().add(item);
        cart.setTotal(8000);

        CanjeConfirmacionDto canje = new CanjeConfirmacionDto(150, 1500, 50);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(puntosClient.confirmarCanje(userId)).thenReturn(canje);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.confirmarCanjePuntos(userId);

        assertThat(cart.getTotal()).isEqualTo(6500); // 8000 - 1500
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    @DisplayName("confirmarCanjePuntos: el total no debería quedar negativo si el descuento supera el total")
    void confirmarCanjePuntos_totalNuncaDebeSerNegativo_cuandoDescuentoSuperaTotal() {
        CartItem item = new CartItem();
        item.setProductId(productId);
        cart.getItems().add(item);
        cart.setTotal(500);

        CanjeConfirmacionDto canje = new CanjeConfirmacionDto(200, 9999, 0); // descuento mayor al total

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(puntosClient.confirmarCanje(userId)).thenReturn(canje);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.confirmarCanjePuntos(userId);

        assertThat(cart.getTotal()).isZero();
        verify(cartRepository, times(1)).save(cart);
    }


    @Test
    @DisplayName("confirmarCanjePuntos: debería lanzar excepción cuando el carrito está vacío")
    void confirmarCanjePuntos_deberiaLanzarExcepcion_cuandoCarritoEstaVacio() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.confirmarCanjePuntos(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("carrito está vacío");

        verify(puntosClient, never()).confirmarCanje(anyLong());
        verify(cartRepository, never()).save(any());
    }

}
