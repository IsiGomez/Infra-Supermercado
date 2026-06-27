package cl.duoc.carrito.service.apis;

import cl.duoc.carrito.dto.remote.CanjeConfirmacionDto;
import cl.duoc.carrito.dto.remote.CanjeSimulacionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "puntos")
public interface PuntosClient {

    @GetMapping("/api/v1/puntos/{usuarioId}/canje/simular")
    CanjeSimulacionDto simularCanje(@PathVariable Long usuarioId);

    @PostMapping("/api/v1/puntos/{usuarioId}/canje/confirmar")
    CanjeConfirmacionDto confirmarCanje(@PathVariable Long usuarioId);
}
