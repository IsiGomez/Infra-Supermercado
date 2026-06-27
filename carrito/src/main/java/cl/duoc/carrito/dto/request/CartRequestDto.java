package cl.duoc.carrito.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CartRequest", description = "DTO para agregar un producto al carrito")
public class CartRequestDto {

    @Schema(description = "Id del producto a agregar", example = "2", required = true)
    @NotNull(message = "El Id del producto es obligatorio")
    @Positive(message = "El Id del producto debe ser un valor positivo")
    private Long productId;

    @Schema(description = "Cantidad a agregar (entre 1 y 99)", example = "10", required = true)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 99, message = "La cantidad no puede superar 99 unidades por producto")
    private Integer quantity;

}
