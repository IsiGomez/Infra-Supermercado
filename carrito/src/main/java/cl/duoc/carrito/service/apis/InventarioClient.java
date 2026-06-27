package cl.duoc.carrito.service.apis;

import cl.duoc.carrito.dto.remote.InventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventario")
public interface InventarioClient {

    @GetMapping("/api/v1/inventory/product/{productId}")
    InventoryDto consultarStock(@PathVariable("productId") Long productId);

}
