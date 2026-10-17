package kaluz.demo.service;

import kaluz.demo.dto.Requests.DocumentoRequest;
import kaluz.demo.dto.Requests.RegistroConductorRequest;
import kaluz.demo.dto.Responses.*;
import kaluz.demo.exception.ConflictException;
import kaluz.demo.model.*;
import kaluz.demo.repository.ConductorRepository;
import kaluz.demo.repository.DocumentoRepository;
import kaluz.demo.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConductorService {

    private final ConductorRepository conductores;
    private final VehiculoRepository vehiculos;
    private final DocumentoRepository documentos;
    private final DocumentoService documentoService;

    public ConductorService(ConductorRepository conductores, VehiculoRepository vehiculos,
                            DocumentoRepository documentos, DocumentoService documentoService) {
        this.conductores = conductores;
        this.vehiculos = vehiculos;
        this.documentos = documentos;
        this.documentoService = documentoService;
    }

    /** Registra el chofer, le asigna su unidad y (opcional) sus documentos. */
    @Transactional
    public RegistroConductorResponse registrar(RegistroConductorRequest req) {
        if (conductores.existsByDni(req.dni())) {
            throw new ConflictException("Ya existe un conductor con DNI " + req.dni());
        }
        String placa = req.vehiculo().placa().trim().toUpperCase();
        if (vehiculos.existsByPlaca(placa)) {
            throw new ConflictException("Ya existe un vehiculo con placa " + placa);
        }

        Conductor c = new Conductor();
        c.setDni(req.dni());
        c.setNombres(req.nombres().trim());
        c.setApellidos(req.apellidos().trim());
        c.setTelefono(req.telefono().trim());
        c = conductores.save(c);

        Vehiculo v = new Vehiculo();
        v.setPlaca(placa);
        v.setTipo(req.vehiculo().tipo());
        v.setMarca(req.vehiculo().marca());
        v.setConductor(c);
        v = vehiculos.save(v);

        List<DocumentoResponse> docs = new ArrayList<>();
        if (req.documentos() != null) {
            for (DocumentoRequest d : req.documentos()) {
                Documento saved = documentos.save(documentoService.nuevo(v, d.tipoDoc(), d.fechaVencimiento()));
                docs.add(DocumentoResponse.of(saved, documentoService.hoy()));
            }
        }
        return new RegistroConductorResponse(ConductorResponse.of(c), VehiculoResponse.of(v), docs);
    }

    @Transactional(readOnly = true)
    public List<ConductorResponse> listar() {
        return conductores.findAll().stream().map(ConductorResponse::of).toList();
    }
}
