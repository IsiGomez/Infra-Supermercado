package cl.supermercado.seguimiento.repository;

import cl.supermercado.seguimiento.model.Seguimiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    List<Seguimiento> findByCompraId(Long compraId);
    List<Seguimiento> findByUsuarioId(Long usuarioId);

}
