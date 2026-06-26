package cl.duoc.inventario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "InventoryResponse", description = "DTO para respuesta sobre un inventario")
public class InventoryResponseDto {

    @Schema(description = "Id del inventario de consulta")
    private Long id;

    @Schema(description = "Id del producto")
    private Long productId;

    @Schema(description = "Nombre del producto")
    private String productName;

    @Schema(description = "Precio unitario del producto")
    private Integer priceUnit;

    @Schema(description = "Cantidad disponible en el inventario")
    private Integer quantity;

}
