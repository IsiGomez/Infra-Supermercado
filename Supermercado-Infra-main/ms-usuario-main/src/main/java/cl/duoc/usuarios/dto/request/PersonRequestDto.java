package cl.duoc.usuarios.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(name = "PersonRequest", description = "DTO para crear o actualizar una persona")
public class PersonRequestDto {

    @Schema(description = "RUT de la persona (formato chileno)", example = "12342784-3", required = true)
    @NotBlank(message = "El rut de la persona es obligatorio")
    @Size(max = 20, message = "El rut no puede superar los 20 caracteres")
    private String rut;

    @Schema(description = "Nombre de la persona", example = "Catalina", required = true)
    @NotBlank(message = "El nombre de la persona es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @Schema(description = "Apellido de la persona", example = "Matus", required = true)
    @NotBlank(message = "El apellido de la persona es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String lastName;

    @Schema(description = "Correo electrónico de la persona", example = "cat.matus@supermercado.cl", required = true)
    @NotBlank(message = "El email de la persona es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @Schema(description = "Teléfono de contacto", example = "912345673")
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    private String phone;

}
