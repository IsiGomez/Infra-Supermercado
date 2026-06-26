package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {

}
