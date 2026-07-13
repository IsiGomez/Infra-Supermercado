package cl.supermercado.notificaciones.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "NotificacionResponseDto", description = "DTO de respuesta de notificación")
public class NotificacionResponseDto {

    @Schema(description = "ID de la notificación", example = "8")
    private Long id;

    @Schema(description = "ID del usuario destinatario", example = "2")
    private Long usuarioId;

    @Schema(description = "Mensaje de la notificación", example = "Tu pedido fue enviado")
    private String mensaje;

    @Schema(description = "Fecha de envio de la notificación", example = "2026-07-11T14:30:00")
    private LocalDateTime fechaEnvio;

    @Schema(description = "Si es que se envio la notificación", example = "true")
    private Boolean enviado;

    @Schema(description = "Si es que se leyo la notificación", example = "false")
    private Boolean leido;

}