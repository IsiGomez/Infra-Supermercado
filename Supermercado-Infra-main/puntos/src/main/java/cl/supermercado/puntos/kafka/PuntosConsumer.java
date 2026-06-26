package cl.supermercado.puntos.kafka;

import cl.supermercado.puntos.event.CompraCompletadaEvent;
import cl.supermercado.puntos.dto.request.PuntosRequestDto;
import cl.supermercado.puntos.service.PuntosService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PuntosConsumer {

    private final PuntosService puntosService;

    @KafkaListener(topics = "compra-completada", groupId = "puntos-group")
    public void onCompraCompletada(CompraCompletadaEvent evento) {
        log.info("Puntos: procesando compra {} para usuario {}, total: ${}",
                evento.getCompraId(), evento.getUsuarioId(), evento.getTotal());

        PuntosRequestDto dto = new PuntosRequestDto(
                evento.getUsuarioId(),
                evento.getCompraId(),
                evento.getTotal()
        );

        puntosService.asignarPuntos(dto);
        log.info("Puntos asignados para usuario {} por compra de ${}", evento.getUsuarioId(), evento.getTotal());
    }

}
