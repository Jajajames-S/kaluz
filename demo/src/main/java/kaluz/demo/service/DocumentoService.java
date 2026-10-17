package kaluz.demo.service;

import kaluz.demo.dto.Requests.DocumentoCreateRequest;
import kaluz.demo.dto.Responses.DocumentoResponse;
import kaluz.demo.exception.NotFoundException;
import kaluz.demo.model.Documento;
import kaluz.demo.model.EstadoDocumento;
import kaluz.demo.model.TipoDocumento;
import kaluz.demo.model.Vehiculo;
import kaluz.demo.repository.DocumentoRepository;
import kaluz.demo.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class DocumentoService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final DocumentoRepository documentos;
    private final VehiculoRepository vehiculos;
    private final int diasAlerta;

    public DocumentoService(DocumentoRepository documentos, VehiculoRepository vehiculos,
                            @Value("${kaluz.documentos.dias-alerta:15}") int diasAlerta) {
        this.documentos = documentos;
        this.vehiculos = vehiculos;
        this.diasAlerta = diasAlerta;
    }

    public LocalDate hoy() {
        return LocalDate.now(LIMA);
    }

    EstadoDocumento calcularEstado(LocalDate vencimiento) {
        LocalDate hoy = hoy();
        if (vencimiento.isBefore(hoy)) return EstadoDocumento.VENCIDO;
        if (!vencimiento.isAfter(hoy.plusDays(diasAlerta))) return EstadoDocumento.POR_VENCER;
        return EstadoDocumento.VIGENTE;
    }

    Documento nuevo(Vehiculo vehiculo, TipoDocumento tipo, LocalDate vencimiento) {
        Documento d = new Documento();
        d.setVehiculo(vehiculo);
        d.setTipoDoc(tipo);
        d.setFechaVencimiento(vencimiento);
        d.setEstadoDoc(calcularEstado(vencimiento));
        return d;
    }

    @Transactional
    public DocumentoResponse crear(DocumentoCreateRequest req) {
        Vehiculo v = vehiculos.findById(req.vehiculoId())
                .orElseThrow(() -> new NotFoundException("Vehiculo " + req.vehiculoId() + " no existe"));
        Documento d = documentos.save(nuevo(v, req.tipoDoc(), req.fechaVencimiento()));
        return DocumentoResponse.of(d, hoy());
    }

    /** Documentos vencidos o que vencen en los proximos {@code diasAlerta} dias. Refresca estado_doc. */
    @Transactional
    public List<DocumentoResponse> alertas() {
        LocalDate hoy = hoy();
        List<Documento> lista = documentos
                .findByFechaVencimientoLessThanEqualOrderByFechaVencimientoAsc(hoy.plusDays(diasAlerta));
        lista.forEach(d -> d.setEstadoDoc(calcularEstado(d.getFechaVencimiento())));
        return lista.stream().map(d -> DocumentoResponse.of(d, hoy)).toList();
    }
}
