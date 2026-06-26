package cl.duoc.carrito.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "InventoryDto", description = "DTO de comunicacion con microservicio inventario")
public class InventoryDto {

    @Schema(description = "Id del inventario")
    private Long id;

    @Schema(description = "Id del producto")
    private Long productId;

    @Schema(description = "Cantidad del producto")
    private Integer quantity;

}
