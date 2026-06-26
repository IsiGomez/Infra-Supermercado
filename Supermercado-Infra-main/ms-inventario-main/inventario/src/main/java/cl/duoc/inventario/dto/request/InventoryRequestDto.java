package cl.duoc.inventario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "InventoryRequest", description = "DTO para actualizar el stock de un producto en inventario")
public class InventoryRequestDto {

    @Schema(description = "ID del registro de inventario", example = "1")
    @NotNull(message = "La ID del inventario es obligatoria")
    private Long id;

    @Schema(description = "ID del producto cuyo stock se actualizará", example = "2", required = true)
    @NotNull(message = "La ID del producto es obligatoria")
    private Long productId;

    @Schema(description = "Nueva cantidad disponible en stock (entre 0 y 150)", example = "50", required = true)
    @NotNull(message = "La cantidad del stock es obligatoria")
    @Min(value = 0, message = "El stock no puede ser menor a 0")
    @Max(value = 150, message = "El stock no puede ser mayor a 150 para un producto")
    private Integer quantity;

}
