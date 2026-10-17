package kaluz.demo.repository;

import kaluz.demo.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
}
