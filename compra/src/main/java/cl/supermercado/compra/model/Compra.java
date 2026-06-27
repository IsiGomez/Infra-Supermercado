package cl.supermercado.compra.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "compra")
@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Double total;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDateTime fechaCompra;

    @Column(nullable = false)
    private Boolean finalizada;

    @Column(name = "pago_confirmado", nullable = false)
    private Boolean pagoConfirmado;

}
