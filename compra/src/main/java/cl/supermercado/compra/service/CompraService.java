package cl.supermercado.compra.service;
import cl.supermercado.compra.dto.request.CompraRequestDto;
import cl.supermercado.compra.dto.response.CompraResponseDto;
import java.util.List;

public interface CompraService {

    CompraResponseDto crearCompra(CompraRequestDto request);
    List<CompraResponseDto> listarCompras();
    List<CompraResponseDto> listarComprasPorUsuario(Long usuarioId);

}
