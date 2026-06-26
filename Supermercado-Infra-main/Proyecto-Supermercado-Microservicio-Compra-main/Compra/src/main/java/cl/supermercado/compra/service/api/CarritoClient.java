package cl.supermercado.compra.service.api;

import cl.supermercado.compra.dto.remote.CartDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "carrito")
public interface CarritoClient {

    @GetMapping("/api/v1/carts/user/{userId}")
    CartDto obtenerCarrito(@PathVariable Long userId);

    @GetMapping("/api/v1/carts/user/{userId}/total")
    Double obtenerTotalCarrito(@PathVariable Long userId);

    @DeleteMapping("/api/v1/carts/user/{userId}/clear")
    void limpiarCarrito(@PathVariable Long userId);

}
