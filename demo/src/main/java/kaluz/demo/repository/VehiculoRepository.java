package kaluz.demo.repository;

import kaluz.demo.model.EstadoConductor;
import kaluz.demo.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);

    List<Vehiculo> findByConductor_EstadoAndLatitudIsNotNullAndLongitudIsNotNull(EstadoConductor estado);
}
