package cl.supermercado.notificaciones.kafka;

import cl.supermercado.notificaciones.event.CompraCompletadaEvent;
import cl.supermercado.notificaciones.dto.request.NotificacionRequestDto;
import cl.supermercado.notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompraKafkaConsumer {

    private final NotificacionService notificacionService;

    @KafkaListener(topics = "compra-completada", groupId = "notificaciones-group")
    public void onCompraCompletada(CompraCompletadaEvent evento) {
        log.info("Notificación: compra {} para usuario {}", evento.getCompraId(), evento.getUsuarioId());

        String mensaje = String.format(
                "¡Tu compra #%d fue procesada exitosamente! Total: $%.2f. " +
                        "Puedes hacer seguimiento de tu pedido en la app.",
                evento.getCompraId(), evento.getTotal()
        );

        notificacionService.enviarNotificacion(
                new NotificacionRequestDto(evento.getUsuarioId(), mensaje)
        );
    }

}
