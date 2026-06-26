package cl.duoc.carrito.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "ProductDto", description = "DTO de comunicacion con microservicio catalogo")
public class ProductDto {

    @Schema(description = "Id del producto")
    private Long id;

    @Schema(description = "Nombre del producto")
    private String name;

    @Schema(description = "Descripcion del producto")
    private String description;

    @Schema(description = "Precio unitario del producto")
    private Integer price;

    @Schema(description = "Id de categoria a la que pertenece")
    private CategoryDto category;

}
