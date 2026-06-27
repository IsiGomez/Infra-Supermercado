package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByRutIgnoreCase(String rut);

    boolean existsByRutIgnoreCaseAndIdNot(String rut, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
