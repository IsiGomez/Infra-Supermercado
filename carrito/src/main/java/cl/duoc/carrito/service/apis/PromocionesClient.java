package cl.duoc.carrito.service.apis;

import cl.duoc.carrito.dto.remote.PromocionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "promociones")
public interface PromocionesClient {

    @GetMapping("/api/v1/promociones/{codigo}")
    PromocionDto obtenerPromocion(@PathVariable String codigo);

}
