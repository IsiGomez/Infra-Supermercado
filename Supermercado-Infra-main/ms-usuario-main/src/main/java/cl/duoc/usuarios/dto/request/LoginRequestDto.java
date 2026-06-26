package cl.duoc.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "LoginRequest", description = "DTO para crear o actualizar una cuenta de acceso")
public class LoginRequestDto {

    @Schema(description = "Nombre de usuario único", example = "FunCata", required = true)
    @NotBlank(message = "El username es obligatorio")
    @Size(max = 100, message = "El username no puede superar los 100 caracteres")
    private String username;

    @Schema(description = "Contraseña (mínimo 6 caracteres, se almacenará encriptada con BCrypt)",
            example = "Cata1234", required = true)
    @NotBlank(message = "El password es obligatorio")
    @Size(min = 6, max = 120, message = "El password debe tener entre 6 y 120 caracteres")
    private String password;

    @Schema(description = "ID de la persona asociada a esta cuenta", example = "3", required = true)
    @NotNull(message = "La persona es obligatoria")
    @Positive(message = "La persona debe ser valida")
    private Long personId;

    @Schema(description = "ID del rol asignado (1=FUNCIONARIO, 2=CLIENTE)", example = "1", required = true)
    @NotNull(message = "El rol es obligatorio")
    @Positive(message = "El rol debe ser valido")
    private Long rolId;

}
