package cl.duoc.carrito.service.impl;

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
import cl.duoc.carrito.service.CartService;
import cl.duoc.carrito.service.apis.CatalogoClient;
import cl.duoc.carrito.service.apis.InventarioClient;
import cl.duoc.carrito.service.apis.PromocionesClient;
import cl.duoc.carrito.service.apis.PuntosClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CatalogoClient catalogoClient;
    private final InventarioClient inventarioClient;
    private final PromocionesClient promocionesClient;
    private final PuntosClient puntosClient;
    private final CartMapper mapper;

    @Override
    @Transactional
    public CartResponseDto getCart(Long userId) {
        log.info("Obteniendo carrito para usuario ID: {}", userId);

        Cart cart = getOrCreateCart(userId);

        log.info("Carrito ID {} retornando para usuario ID: {}", cart.getId(), userId);
        return mapper.toDto(cart, fetchProducts(cart));

    }

    @Override
    @Transactional
    public CartResponseDto addItem(Long userId, CartRequestDto request) {
        log.info("Agregando producto ID: {} al carrito del usuario ID: {}", request.getProductId(), userId);

        ProductDto product = catalogoClient.getProductById(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException(
                    "El producto con ID " + request.getProductId() + " no existe en el catálogo");
        }
        log.info("Producto '{}' encontrado en el catalogo", product.getName());

        InventoryDto stock = inventarioClient.consultarStock(request.getProductId());
        if (stock.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Stock insuficiente para el producto '" + product.getName() + "'. " +
                            "Stock disponible: " + stock.getQuantity());
        }

        Cart cart = getOrCreateCart(userId);

        cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .ifPresentOrElse(
                        existingItem -> {
                            throw new IllegalArgumentException("El producto '" + product.getName() + "' ya esta en el carrito." +
                                    "Usa la opcion de actualizar cantidad si desea modificarlo o remover Item si quieres eliminarlo.");
                        },
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setProductId(request.getProductId());
                            newItem.setQuantity(request.getQuantity());
                            newItem.calcularSubtotal(product.getPrice());
                            cartItemRepository.save(newItem);
                            log.info("Nuevo item agregado: producto '{}', cantidad: {}", product.getName(), request.getQuantity());
                        }
                );

        cart.calcularTotal();
        cartRepository.save(cart);
        log.info("Total del carrito ID {} recalculado: {}", cart.getId(), cart.getTotal());

        return mapper.toDto(cart, fetchProducts(cart));
    }

    @Override
    @Transactional
    public CartResponseDto updateQuantity(Long userId, Long productId, CartItemRequestDto request) {
        log.info("Actualizando cantidad del producto ID: {} en carrito del usuario ID: {}", productId, userId);

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El producto con ID " + productId + " no está en el carrito"));

        InventoryDto stock = inventarioClient.consultarStock(productId);
        if (stock.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Stock disponible: " + stock.getQuantity());
        }

        ProductDto product = catalogoClient.getProductById(productId);
        item.setQuantity(request.getQuantity());
        item.calcularSubtotal(product.getPrice());
        cartItemRepository.save(item);

        cart.calcularTotal();
        cartRepository.save(cart);
        log.info("Cantidad actualizada para producto '{}'. Nueva cantidad: {}, nuevo Subtotal: {}",
                  product.getName(), request.getQuantity(), item.getSubtotal());

        return mapper.toDto(cart, fetchProducts(cart));
    }

    @Override
    @Transactional
    public CartResponseDto removeItem(Long userId, Long productId) {
        log.info("Eliminando producto ID: {} del carrito del usuario ID: {}", productId, userId);

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "El producto con ID " + productId + " no esta en el carrito")
                );

        cart.getItems().remove(item);
        cart.calcularTotal();
        cartRepository.save(cart);
        log.info("Producto ID: {} eliminando del carrito ID: {}, Nuevo total: {}",
                  productId, cart.getId(), cart.getTotal());

        return mapper.toDto(cart, fetchProducts(cart));
    }


    @Override
    public CartResponseDto aplicarPromocion(Long userId, String codigo) {
        PromocionDto promo = promocionesClient.obtenerPromocion(codigo);
        if (promo == null || !promo.isVigente()) {
            throw new IllegalArgumentException("Promoción inválida o expirada");
        }

        Cart cart = getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío, no se puede aplicar una promoción");
        }

        double descuento = cart.getTotal() * (promo.getDescuento() / 100.0);
        cart.setTotal((int) (cart.getTotal() - descuento));
        cartRepository.save(cart);
        return mapper.toDto(cart, fetchProducts(cart));
    }


    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Limpiando carrito del usuario ID: {}", userId);

        Cart cart = getOrCreateCart(userId);

        cart.getItems().clear();
        cart.setTotal(0);
        cartRepository.save(cart);
        log.info("Carrito ID: {} limpiando para usuario ID: {}", cart.getId(), userId);
    }


    @Override
    @Transactional(readOnly = true)
    public SimulacionCanjeResponseDto simularCanjePuntos(Long userId) {
        log.info("Simulando canje de puntos para usuario ID: {}", userId);

        CanjeSimulacionDto simulacion = puntosClient.simularCanje(userId);
        Cart cart = getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío, no hay nada que pagar.");
        }

        int totalActual = cart.getTotal();
        int descuento = simulacion.getMontoDescuento() != null ? simulacion.getMontoDescuento() : 0;
        int totalConDescuento = Math.max(0, totalActual - descuento);

        return new SimulacionCanjeResponseDto(
                simulacion.getPuntosDisponibles(),
                simulacion.getPuntosCanjeables(),
                descuento,
                simulacion.isPuedeCanjear(),
                totalActual,
                totalConDescuento,
                simulacion.getMensaje()
        );
    }


    @Override
    @Transactional
    public CartResponseDto confirmarCanjePuntos(Long userId) {
        log.info("Confirmando canje de puntos para usuario ID: {}", userId);

        Cart cart = getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío, no hay nada que pagar.");
        }

        CanjeConfirmacionDto canje = puntosClient.confirmarCanje(userId);

        int nuevoTotal = Math.max(0, cart.getTotal() - canje.getMontoDescuento());
        cart.setTotal(nuevoTotal);
        cartRepository.save(cart);

        log.info("Canje aplicado al carrito: usuario={}, descuento=${}, totalFinal=${}",
                userId, canje.getMontoDescuento(), nuevoTotal);

        return mapper.toDto(cart, fetchProducts(cart));
    }


    // -- Metodos privados --
    private Cart getOrCreateCart(Long userId){
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    private Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setTotal(0);

        Cart saved = cartRepository.save(cart);
        log.info("Carrito ID: {} creado para usuario ID: {}",
                saved.getId(), userId);

        return saved;
    }

    private Map<Long, ProductDto> fetchProducts(Cart cart) {
        List<Long> ids = cart.getItems().stream()
                .map((CartItem::getProductId))
                .toList();

        if (ids.isEmpty()) {
            log.info("Carrito ID: {} sin items, no se consulta catalogo",
                    cart.getId());

            return Map.of();
        }

        log.info("Consultando catalogo para {} productos del carrito ID: {}",
                ids.size(), cart.getId());

        return catalogoClient.getProductsByIds(ids)
                .stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));
    }

}
