package cl.duoc.catalogo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "ExceptionDto", description = "DTO en caso de recibir un error")
public class ExceptionDto {

    @Schema(description = "Mensaje del error")
    private String message;

    @Schema(description = "Descripción del error")
    private String description;

}
