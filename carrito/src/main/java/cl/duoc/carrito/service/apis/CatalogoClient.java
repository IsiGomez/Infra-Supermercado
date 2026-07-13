package cl.duoc.carrito.service.apis;

import cl.duoc.carrito.dto.remote.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "catalogo")
public interface CatalogoClient {

    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable Long id);

    @GetMapping("/api/v1/products/by-ids")
    CollectionModel<EntityModel<ProductDto>> getProductsByIds(@RequestParam List<Long> ids);

}
