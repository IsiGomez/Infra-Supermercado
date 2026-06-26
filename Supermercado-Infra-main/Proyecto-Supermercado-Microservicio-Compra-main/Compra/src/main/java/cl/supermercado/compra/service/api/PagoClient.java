package cl.supermercado.compra.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pago")
public interface PagoClient {

    @GetMapping("/api/v1/pagos/usuario/{usuarioId}/ultimo-exitoso")
    Boolean tieneUltimoPagoExitoso(@PathVariable Long usuarioId);

}
