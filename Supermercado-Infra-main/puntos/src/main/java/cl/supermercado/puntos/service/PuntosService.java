package cl.supermercado.puntos.service;

import cl.supermercado.puntos.dto.request.PuntosRequestDto;
import cl.supermercado.puntos.dto.response.CanjeConfirmacionResponseDto;
import cl.supermercado.puntos.dto.response.CanjeSimulacionResponseDto;
import cl.supermercado.puntos.dto.response.PuntosResponseDto;

public interface PuntosService {

    PuntosResponseDto asignarPuntos(PuntosRequestDto request);
    PuntosResponseDto consultarPuntos(Long usuarioId);

    CanjeSimulacionResponseDto simularCanje(Long usuarioId);
    CanjeConfirmacionResponseDto confirmarCanje(Long usuarioId);

}
