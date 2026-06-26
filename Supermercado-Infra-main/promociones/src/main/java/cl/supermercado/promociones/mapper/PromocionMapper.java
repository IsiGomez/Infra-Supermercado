package cl.supermercado.promociones.mapper;
import cl.supermercado.promociones.dto.request.PromocionRequestDto;
import cl.supermercado.promociones.dto.response.PromocionResponseDto;
import cl.supermercado.promociones.model.Promocion;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class PromocionMapper {

    public Promocion toEntity(PromocionRequestDto dto) {
        Promocion p = new Promocion();
        p.setCodigo(dto.getCodigo());
        p.setDescuento(dto.getDescuento());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setAcumulable(dto.getAcumulable());
        return p;
    }

    public PromocionResponseDto toDto(Promocion p) {
        return new PromocionResponseDto(
                p.getId(),
                p.getCodigo(),
                p.getDescuento(),
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getAcumulable()
        );
    }

}