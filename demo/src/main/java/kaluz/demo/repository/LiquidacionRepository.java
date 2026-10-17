package kaluz.demo.repository;

import kaluz.demo.model.EstadoPago;
import kaluz.demo.model.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiquidacionRepository extends JpaRepository<Liquidacion, Long> {
    Optional<Liquidacion> findByConductorIdAndEstadoPago(Long conductorId, EstadoPago estadoPago);

    List<Liquidacion> findByConductorIdOrderByCreatedAtDesc(Long conductorId);
}
