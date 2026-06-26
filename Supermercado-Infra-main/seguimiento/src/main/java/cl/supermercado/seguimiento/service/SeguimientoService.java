package cl.supermercado.seguimiento.service;

import cl.supermercado.seguimiento.dto.request.SeguimientoRequestDto;
import cl.supermercado.seguimiento.dto.response.SeguimientoResponseDto;

import java.util.List;

public interface SeguimientoService {

    SeguimientoResponseDto registrarSeguimiento(SeguimientoRequestDto request);

    List<SeguimientoResponseDto> listarSeguimientos();
    List<SeguimientoResponseDto> historialPorCompra(Long compraId);
    List<SeguimientoResponseDto> listarPorUsuario(Long usuarioId);

}
