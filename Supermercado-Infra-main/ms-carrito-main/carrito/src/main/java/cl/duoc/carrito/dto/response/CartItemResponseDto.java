package cl.duoc.carrito.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CartItemResponseDto", description = "DTO de respuesta de un item del carrito")
public class CartItemResponseDto {

    @Schema(description = "Id del item del carrito")
    private Long id;

    @Schema(description = "Id del producto")
    private Long productId;

    @Schema(description = "Nombre del producto")
    private String productName;

    @Schema(description = "Cantidad del producto")
    private Integer quantity;

    @Schema(description = "Subtotal a pagar")
    private Integer subtotal;

}
