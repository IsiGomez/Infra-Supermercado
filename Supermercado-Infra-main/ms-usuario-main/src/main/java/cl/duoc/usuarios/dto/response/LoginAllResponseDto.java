package cl.duoc.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "LoginAllResponse", description = "DTO para respuesta completa sobre un login")
public class LoginAllResponseDto {

    @Schema(description = "Id del login de consulta")
    private Long id;

    @Schema(description = "Nombre del usuario")
    private String username;

    @Schema(description = "Información sobre la persona asociada al login")
    private PersonResponseDto person;

    @Schema(description = "Información sobre el rol asignado al login")
    private RolResponseDto rol;

}
