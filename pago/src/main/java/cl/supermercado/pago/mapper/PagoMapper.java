package cl.supermercado.pago.mapper;

import cl.supermercado.pago.dto.response.PagoResponseDto;
import cl.supermercado.pago.model.Pago;
import org.springframework.stereotype.Component;

@Component
public class PagoMapper {

    public PagoResponseDto toDto(Pago pago) {
        return new PagoResponseDto(
                pago.getId(),
                pago.getUsuarioId(),
                pago.getMonto(),
                pago.getMetodo(),
                pago.getExitoso(),
                pago.getFechaPago()
        );
    }

}
