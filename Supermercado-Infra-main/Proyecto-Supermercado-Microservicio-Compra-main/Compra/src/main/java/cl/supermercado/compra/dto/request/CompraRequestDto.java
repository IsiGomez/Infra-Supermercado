package cl.supermercado.compra.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
@Schema(name = "CompraRequestDto", description = "DTO para confirmar una compra")
public class CompraRequestDto {

    @Schema(description = "Id del usuario que confirma la compra (debe coincidir con el del JWT)",
            example = "2", required = true)
    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

}
