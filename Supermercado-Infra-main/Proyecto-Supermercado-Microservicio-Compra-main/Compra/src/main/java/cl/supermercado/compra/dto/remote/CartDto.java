package cl.supermercado.compra.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CartDto", description = "DTO de la información de un carrito")
public class CartDto {

    @Schema(description = "Id del carrito")
    private Long id;

    @Schema(description = "Id del usuario")
    private Long userId;

    @Schema(description = "Items dentro del carrito")
    private List<CartItemDto> items;

    @Schema(description = "Total a pagar")
    private Integer total;

}
