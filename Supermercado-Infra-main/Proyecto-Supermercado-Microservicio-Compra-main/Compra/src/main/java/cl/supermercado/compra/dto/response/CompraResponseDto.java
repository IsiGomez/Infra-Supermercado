package cl.supermercado.compra.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CompraResponseDto", description = "DTO de respuesta sobre compra")
public class CompraResponseDto {

    @Schema(description = "Id de la compra")
    private Long id;

    @Schema(description = "Id del usuario")
    private Long usuarioId;

    @Schema(description = "Total de la compra")
    private Double total;

    @Schema(description = "Fecha de la compra")
    private LocalDateTime fechaCompra;

    @Schema(description = "Si es que la compra se finalizo")
    private Boolean finalizada;

    @Schema(description = "Si es que el pago esta confirmado")
    private Boolean pagoConfirmado;

}