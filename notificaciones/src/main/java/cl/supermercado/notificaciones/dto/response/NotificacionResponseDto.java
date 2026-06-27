package cl.supermercado.notificaciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class NotificacionResponseDto {

    private Long id;
    private Long usuarioId;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private Boolean enviado;
    private Boolean leido;

}