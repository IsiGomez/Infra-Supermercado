package cl.supermercado.seguimiento.kafka;

import cl.supermercado.seguimiento.event.CompraCompletadaEvent;
import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.service.SeguimientoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeguimientoKafkaConsumer {

    private final SeguimientoService seguimientoService;

    @KafkaListener(topics = "compra-completada", groupId = "seguimiento-group")
    public void onCompraCompletada(CompraCompletadaEvent evento) {
        log.info("Seguimiento: creando registro PENDIENTE para compra {} usuario {}",
                evento.getCompraId(), evento.getUsuarioId());

        SeguimientoRequestDto dto = new SeguimientoRequestDto(
                evento.getCompraId(),
                evento.getUsuarioId(),
                "PENDIENTE"
        );

        seguimientoService.registrarSeguimiento(dto);
    }

}
