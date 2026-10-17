package kaluz.demo.repository;

import kaluz.demo.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    boolean existsByDni(String dni);
}
