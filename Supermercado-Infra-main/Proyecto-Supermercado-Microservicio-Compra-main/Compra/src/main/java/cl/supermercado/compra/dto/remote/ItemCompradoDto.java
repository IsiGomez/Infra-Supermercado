package cl.supermercado.compra.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "ItemCompraDto", description = "DTO sobre la información de un item")
public class ItemCompradoDto {

    @Schema(description = "Id del producto")
    private Long productId;

    @Schema(description = "Cantidad del producto")
    private Integer quantity;

}
