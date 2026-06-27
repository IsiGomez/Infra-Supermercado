package cl.supermercado.promociones.repository;
import cl.supermercado.promociones.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    Optional<Promocion> findByCodigo(String codigo);

}
