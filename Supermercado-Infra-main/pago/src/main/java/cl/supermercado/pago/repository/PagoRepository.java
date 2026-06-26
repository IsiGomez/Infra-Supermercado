package cl.supermercado.pago.repository;
import cl.supermercado.pago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findTopByUsuarioIdAndExitosoTrueOrderByFechaPagoDesc(Long usuarioId);

}