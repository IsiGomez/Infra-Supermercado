package cl.supermercado.promociones.dto.response;
import lombok.*;
import java.time.LocalDate;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class PromocionResponseDto {

    private Long id;
    private String codigo;
    private Double descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean acumulable;

}