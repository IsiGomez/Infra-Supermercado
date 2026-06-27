package cl.duoc.inventario.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario")
@Getter             @Setter
@AllArgsConstructor @NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_Id", nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

}
