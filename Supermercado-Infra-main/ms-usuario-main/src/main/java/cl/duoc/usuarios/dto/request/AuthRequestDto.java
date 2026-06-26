package cl.duoc.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "AuthRequestDto", description = "DTO para obtener el token de autenticacion")
public class AuthRequestDto {

    @Schema(description = "Nombre de usuario registrado", example = "FunClaudia", required = true)
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Schema(description = "Contraseña del usuario registrado", example = "304Claudi", required = true)
    @NotBlank(message = "El password es obligatorio")
    private String password;

}
