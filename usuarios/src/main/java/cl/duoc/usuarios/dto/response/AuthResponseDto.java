package cl.duoc.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "AuthResponse", description = "DTO para respuesta a solicitud de autenticación")
public class AuthResponseDto {

    @Schema(description = "Token de autenticacion del usuario")
    private String token;

    @Schema(description = "Nombre del usuario que solicito el token")
    private String username;

    @Schema(description = "Tipo de rol del usuario")
    private String rol;

}
