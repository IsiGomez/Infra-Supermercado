package cl.duoc.carrito.mapper;

import cl.duoc.carrito.dto.remote.ProductDto;
import cl.duoc.carrito.dto.response.CartItemResponseDto;
import cl.duoc.carrito.dto.response.CartResponseDto;
import cl.duoc.carrito.model.Cart;
import cl.duoc.carrito.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CartMapper {

    public CartResponseDto toDto(Cart cart, Map<Long, ProductDto> products) {
        if (cart == null) {
            return null;
        }

        List<CartItemResponseDto> itemsDto = cart.getItems().stream()
                .map(item -> toItemDto(item, products.get(item.getProductId())))
                .toList();

        CartResponseDto response = new CartResponseDto();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setItems(itemsDto);
        response.setTotal(cart.getTotal());

        return response;
    }

    public CartItemResponseDto toItemDto(CartItem item, ProductDto product) {
        if (item == null) {
            return null;
        }

        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(product != null ? product.getName() : "Producto no disponible");
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());

        return dto;
    }

}
