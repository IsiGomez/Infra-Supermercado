package cl.supermercado.pago.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "carrito")
public interface CarritoClient {

    @GetMapping("/api/v1/carts/user/{userId}/total")
    Double obtenerTotalCarrito(@PathVariable Long userId);

}
