package cl.supermercado.compra.mapper;

import cl.supermercado.compra.dto.response.CompraResponseDto;
import cl.supermercado.compra.model.Compra;
import org.springframework.stereotype.Component;

@Component
public class CompraMapper {

    public CompraResponseDto toDto(Compra compra) {
        return new CompraResponseDto(
                compra.getId(),
                compra.getUsuarioId(),
                compra.getTotal(),
                compra.getFechaCompra(),
                compra.getFinalizada(),
                compra.getPagoConfirmado()
        );
    }
}
