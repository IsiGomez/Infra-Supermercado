package cl.duoc.inventario.event;

import cl.duoc.inventario.dto.remote.ItemCompradoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class CompraCompletadaEvent {

    private Long compraId;
    private Long usuarioId;
    private Double total;
    private LocalDateTime fechaCompra;
    private List<ItemCompradoDto> items;

}
