package cl.supermercado.pago.service;
import cl.supermercado.pago.dto.request.PagoRequestDto;
import cl.supermercado.pago.dto.response.PagoResponseDto;
import java.util.List;

public interface PagoService {

    PagoResponseDto procesarPago(PagoRequestDto request);
    List<PagoResponseDto> listarPagos();
    Boolean tieneUltimoPagoExitoso(Long usuarioId);
    PagoResponseDto obtenerUltimoPagoExitoso(Long usuarioId);
    PagoResponseDto obtenerPorId(Long id);

}