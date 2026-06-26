package cl.supermercado.puntos.repository;

import cl.supermercado.puntos.model.PuntosHistorial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuntosHistorialRepository extends JpaRepository<PuntosHistorial, Long> {

    boolean existsByCompraId(Long compraId);

}
