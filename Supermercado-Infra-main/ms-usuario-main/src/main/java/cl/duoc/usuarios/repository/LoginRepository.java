package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    boolean existsByPersonId(Long personId);

    boolean existsByPersonIdAndIdNot(Long personId, Long id);

    Optional<Login> findByUsernameIgnoreCase(String username);

}
