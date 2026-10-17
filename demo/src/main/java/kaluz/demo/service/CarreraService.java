package kaluz.demo.service;

import kaluz.demo.dto.Requests.CarreraRequest;
import kaluz.demo.dto.Responses.CarreraResponse;
import kaluz.demo.exception.NotFoundException;
import kaluz.demo.model.*;
import kaluz.demo.repository.CarreraRepository;
import kaluz.demo.repository.ConductorRepository;
import kaluz.demo.repository.LiquidacionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CarreraService {

    private final CarreraRepository carreras;
    private final ConductorRepository conductores;
    private final LiquidacionRepository liquidaciones;
    private final BigDecimal porcentaje;

    public CarreraService(CarreraRepository carreras, ConductorRepository conductores,
                          LiquidacionRepository liquidaciones,
                          @Value("${kaluz.comision.porcentaje:0.05}") BigDecimal porcentaje) {
        this.carreras = carreras;
        this.conductores = conductores;
        this.liquidaciones = liquidaciones;
        this.porcentaje = porcentaje;
    }

    public BigDecimal calcularComision(BigDecimal precio) {
        return precio.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Registra el viaje con comision = precio * 5 %. Si la carrera esta COMPLETADA (por defecto),
     * la comision se suma a la liquidacion PENDIENTE del conductor (se crea si no existe).
     */
    @Transactional
    public CarreraResponse registrar(CarreraRequest req) {
        Conductor conductor = conductores.findById(req.conductorId())
                .orElseThrow(() -> new NotFoundException("Conductor " + req.conductorId() + " no existe"));

        Carrera c = new Carrera();
        c.setConductor(conductor);
        c.setPrecio(req.precio());
        c.setComisionEmpresa(calcularComision(req.precio()));
        if (req.estado() != null) c.setEstado(req.estado());
        c = carreras.save(c);

        if (c.getEstado() == EstadoCarrera.COMPLETADA) {
            Liquidacion l = liquidaciones.findByConductorIdAndEstadoPago(conductor.getId(), EstadoPago.PENDIENTE)
                    .orElseGet(() -> {
                        Liquidacion nueva = new Liquidacion();
                        nueva.setConductor(conductor);
                        return nueva;
                    });
            l.setComisionAcumulada5(l.getComisionAcumulada5().add(c.getComisionEmpresa()));
            liquidaciones.save(l);
        }
        return CarreraResponse.of(c);
    }
}
