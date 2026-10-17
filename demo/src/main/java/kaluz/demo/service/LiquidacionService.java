package kaluz.demo.service;

import kaluz.demo.dto.Responses.ConductorResponse;
import kaluz.demo.dto.Responses.DeudaConductorResponse;
import kaluz.demo.dto.Responses.LiquidacionResponse;
import kaluz.demo.exception.ConflictException;
import kaluz.demo.exception.NotFoundException;
import kaluz.demo.model.Conductor;
import kaluz.demo.model.EstadoPago;
import kaluz.demo.model.Liquidacion;
import kaluz.demo.repository.ConductorRepository;
import kaluz.demo.repository.LiquidacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class LiquidacionService {

    private final LiquidacionRepository liquidaciones;
    private final ConductorRepository conductores;

    public LiquidacionService(LiquidacionRepository liquidaciones, ConductorRepository conductores) {
        this.liquidaciones = liquidaciones;
        this.conductores = conductores;
    }

    @Transactional(readOnly = true)
    public DeudaConductorResponse deudaDeConductor(Long conductorId) {
        Conductor c = conductores.findById(conductorId)
                .orElseThrow(() -> new NotFoundException("Conductor " + conductorId + " no existe"));
        List<Liquidacion> lista = liquidaciones.findByConductorIdOrderByCreatedAtDesc(conductorId);
        BigDecimal total = lista.stream()
                .filter(l -> l.getEstadoPago() == EstadoPago.PENDIENTE)
                .map(Liquidacion::getComisionAcumulada5)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new DeudaConductorResponse(ConductorResponse.of(c), total,
                lista.stream().map(LiquidacionResponse::of).toList());
    }

    /** Cierra el corte contable: marca la liquidacion como PAGADO. */
    @Transactional
    public LiquidacionResponse pagar(Long id) {
        Liquidacion l = liquidaciones.findById(id)
                .orElseThrow(() -> new NotFoundException("Liquidacion " + id + " no existe"));
        if (l.getEstadoPago() == EstadoPago.PAGADO) {
            throw new ConflictException("La liquidacion " + id + " ya fue pagada");
        }
        l.setEstadoPago(EstadoPago.PAGADO);
        l.setFechaPago(Instant.now());
        return LiquidacionResponse.of(l);
    }
}
