package cl.duoc.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "RolResponse", description = "DTO para respuesta sobre informacion de un rol")
public class RolResponseDto {

    @Schema(description = "Id del rol")
    private Long id;

    @Schema(description = "Nombre perteneciente a la ID del rol")
    private String name;

    @Schema(description = "Descripcion del tipo de rol")
    private String description;

}
