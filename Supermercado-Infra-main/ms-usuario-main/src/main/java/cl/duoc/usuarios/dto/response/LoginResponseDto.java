package cl.duoc.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "LoginResponse", description = "DTO para respuesta para la consulta por el rol de un login")
public class LoginResponseDto {

    @Schema(description = "Id del login que se consulto")
    private Long id;

    @Schema(description = "Id del nombre de usuario asociado a la consulta")
    private String username;

    @Schema(description = "Id del rol asignado al login que se consulta")
    private RolResponseDto rol;

}
