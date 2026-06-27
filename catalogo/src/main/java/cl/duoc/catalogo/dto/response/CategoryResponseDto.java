package cl.duoc.catalogo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(description = "DTO de respuesta de categoría")
public class CategoryResponseDto {

    @Schema(description = "ID de la categoría", example = "3")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Lácteos")
    private String name;

}