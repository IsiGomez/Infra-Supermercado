package cl.duoc.carrito.dto.request;

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
@Schema(name = "CartItemRequest", description = "DTO para actualizar la cantidad de un producto ya existente en el carrito")
public class CartItemRequestDto {

    @Schema(description = "Nueva cantidad del producto (entre 1 y 99)", example = "5", required = true)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 99, message = "La cantidad no puede superar 99 unidades por producto")
    private Integer quantity;

}
