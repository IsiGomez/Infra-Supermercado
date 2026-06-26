package cl.supermercado.compra.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CartItemDto", description = "DTO de un item en el carrito")
public class CartItemDto {

    @Schema(description = "Id del carrito")
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
