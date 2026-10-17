package kaluz.demo.repository;

import kaluz.demo.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByFechaVencimientoLessThanEqualOrderByFechaVencimientoAsc(LocalDate limite);
}
