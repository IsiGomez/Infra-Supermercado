package cl.supermercado.puntos.repository;
import cl.supermercado.puntos.model.Puntos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PuntosRepository extends JpaRepository<Puntos, Long> {

    Optional<Puntos> findByUsuarioId(Long usuarioId);

}
