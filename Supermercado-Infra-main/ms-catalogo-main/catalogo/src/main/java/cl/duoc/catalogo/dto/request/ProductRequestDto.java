package cl.duoc.catalogo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "ProductRequest", description = "DTO para crear o actualizar un producto del catálogo")
public class ProductRequestDto {

    @Schema(description = "Nombre del producto", example = "Leche Descremada 1L", required = true)
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres")
    private String name;

    @Schema(description = "Descripción del producto", example = "Leche descremada sin lactosa, botella de 1 litro")
    @Size(max = 280, message = "La descripcion no puede superar los 280 caracteres")
    private String description;

    @Schema(description = "Precio unitario en pesos chilenos", example = "1290", required = true)
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    @Max(value = 1000000, message = "El precio no puede superar los 1.000.000")
    private Integer price;

    @Schema(description = "ID de la categoría a la que pertenece el producto", example = "2", required = true)
    @NotNull(message = "La categoria es obligatoria")
    @Positive(message = "La categoria debe ser valida")
    private Long categoryId;

}