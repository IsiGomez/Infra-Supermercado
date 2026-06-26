package cl.duoc.usuarios.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "PersonResponse", description = "DTO para respuesta sobre informacion de una persona")
public class PersonResponseDto {

    @Schema(description = "Id de la persona")
    private Long id;

    @Schema(description = "Rut perteneciente a la persona")
    private String rut;

    @Schema(description = "Nombre de la persona")
    private String name;

    @Schema(description = "Apellido de la persona")
    private String lastName;

    @Schema(description = "Email asociado a la persona")
    private String email;

    @Schema(description = "Telefono asociado a la persona")
    private String phone;

}
