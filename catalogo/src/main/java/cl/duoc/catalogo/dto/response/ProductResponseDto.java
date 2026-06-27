package cl.duoc.catalogo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "ExceptionDto", description = "DTO de respuesta de producto")
public class ProductResponseDto {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Leche de Chololate")
    private String name;

    @Schema(description = "Descripcion del producto", example = "Caja de carton de un litro de leche entera sabor chocolate")
    private String description;

    @Schema(description = "Precio en pesos chilenos", example = "1200")
    private Integer price;

    @Schema(description = "ID de la categoría", example = "2")
    private CategoryResponseDto category;

}