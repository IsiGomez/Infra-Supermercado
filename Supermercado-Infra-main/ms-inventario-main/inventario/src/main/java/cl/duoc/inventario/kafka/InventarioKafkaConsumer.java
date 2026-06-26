package cl.duoc.inventario.kafka;

import cl.duoc.inventario.dto.remote.ItemCompradoDto;
import cl.duoc.inventario.event.CompraCompletadaEvent;
import cl.duoc.inventario.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventarioKafkaConsumer {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "compra-completada", groupId = "inventario-group")
    public void onCompraCompletada(CompraCompletadaEvent evento) {
        log.info("Inventario: descontando stock para {} productos de la compra {}",
                evento.getItems() != null ? evento.getItems().size() : 0, evento.getCompraId());

        if (evento.getItems() == null) return;

        for (ItemCompradoDto item : evento.getItems()) {
            inventoryRepository.findByProductId(item.getProductId()).ifPresentOrElse(
                    inv -> {
                        int nuevoStock = Math.max(0, inv.getQuantity() - item.getQuantity());
                        inv.setQuantity(nuevoStock);
                        inventoryRepository.save(inv);
                        log.info("Stock producto {}: {} → {}", item.getProductId(),
                                inv.getQuantity() + item.getQuantity(), nuevoStock);
                    },
                    () -> log.warn("Producto {} no encontrado en inventario", item.getProductId())
            );
        }
    }

}
