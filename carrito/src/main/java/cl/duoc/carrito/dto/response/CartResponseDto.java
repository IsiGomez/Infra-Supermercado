package cl.duoc.carrito.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CartResponseDto", description = "DTO de respuesta del carrito")
public class CartResponseDto {

    @Schema(description = "Id del carrito")
    private Long id;

    @Schema(description = "Id de la persona")
    private Long userId;

    @Schema(description = "Lista de items que contiene")
    private List<CartItemResponseDto> items;

    @Schema(description = "Total a pagar")
    private Integer total;

}
