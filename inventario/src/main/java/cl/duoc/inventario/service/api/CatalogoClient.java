package cl.duoc.inventario.service.api;

import cl.duoc.inventario.dto.remote.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalogo")
public interface CatalogoClient {

    @GetMapping("/api/v1/products/{id}")
    ProductDto obtenerProductoPorId(@PathVariable Long id);

}
