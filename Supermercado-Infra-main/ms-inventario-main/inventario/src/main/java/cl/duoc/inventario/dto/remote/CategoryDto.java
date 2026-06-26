package cl.duoc.inventario.dto.remote;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CategoryDto", description = "DTO remoto de comunicación de Category")
public class CategoryDto {

    @Schema(description = "Id de la categoria")
    private Long id;

    @Schema(description = "Nombre de la categoria")
    private String name;

}
