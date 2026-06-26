package cl.supermercado.pago.dto.response;
import lombok.*;
import java.time.LocalDateTime;


@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class PagoResponseDto {

    private Long id;
    private Long usuarioId;
    private Double monto;
    private String metodo;
    private Boolean exitoso;
    private LocalDateTime fechaPago;

}
