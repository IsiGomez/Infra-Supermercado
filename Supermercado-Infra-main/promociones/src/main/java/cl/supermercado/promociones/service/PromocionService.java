package cl.supermercado.promociones.service;

import cl.supermercado.promociones.dto.request.PromocionRequestDto;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;

import java.util.List;

public interface PromocionService {

    PromocionResponseDto crearPromocion(PromocionRequestDto request);
    List<PromocionResponseDto> listarPromociones();
    PromocionResponseDto obtenerPorCodigo(String codigo);
    List<PromocionResponseDto> listarVigentes();

}
