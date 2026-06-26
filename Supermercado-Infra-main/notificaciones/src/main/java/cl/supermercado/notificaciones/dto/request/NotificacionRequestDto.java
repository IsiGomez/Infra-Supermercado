package cl.supermercado.notificaciones.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "NotificacionRequest", description = "DTO para enviar una notificación a un usuario")
public class NotificacionRequestDto {

    @Schema(description = "ID del usuario destinatario", example = "2", required = true)
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "Contenido del mensaje", example = "Tu compra #15 ha sido enviada", required = true)
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

}
