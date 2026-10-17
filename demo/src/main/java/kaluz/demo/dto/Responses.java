package kaluz.demo.dto;

import kaluz.demo.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class Responses {
    private Responses() {}

    public record ConductorResponse(Long id, String dni, String nombres, String apellidos,
                                    String telefono, EstadoConductor estado) {
        public static ConductorResponse of(Conductor c) {
            return new ConductorResponse(c.getId(), c.getDni(), c.getNombres(), c.getApellidos(),
                    c.getTelefono(), c.getEstado());
        }
    }

    public record VehiculoResponse(Long id, String placa, TipoVehiculo tipo, String marca, Long conductorId,
                                   Double latitud, Double longitud, Instant ultimaActualizacionGps) {
        public static VehiculoResponse of(Vehiculo v) {
            return new VehiculoResponse(v.getId(), v.getPlaca(), v.getTipo(), v.getMarca(),
                    v.getConductor() == null ? null : v.getConductor().getId(),
                    v.getLatitud(), v.getLongitud(), v.getUltimaActualizacionGps());
        }
    }

    public record DocumentoResponse(Long id, Long vehiculoId, String placa, TipoDocumento tipoDoc,
                                    LocalDate fechaVencimiento, EstadoDocumento estadoDoc, long diasRestantes) {
        public static DocumentoResponse of(Documento d, LocalDate hoy) {
            return new DocumentoResponse(d.getId(), d.getVehiculo().getId(), d.getVehiculo().getPlaca(),
                    d.getTipoDoc(), d.getFechaVencimiento(), d.getEstadoDoc(),
                    java.time.temporal.ChronoUnit.DAYS.between(hoy, d.getFechaVencimiento()));
        }
    }

    public record RegistroConductorResponse(ConductorResponse conductor, VehiculoResponse vehiculo,
                                            List<DocumentoResponse> documentos) {}

    public record VehiculoCercanoResponse(VehiculoResponse vehiculo, ConductorResponse conductor,
                                          double distanciaKm) {}

    public record CarreraResponse(Long id, Long conductorId, BigDecimal precio, BigDecimal comisionEmpresa,
                                  EstadoCarrera estado, Instant createdAt) {
        public static CarreraResponse of(Carrera c) {
            return new CarreraResponse(c.getId(), c.getConductor().getId(), c.getPrecio(),
                    c.getComisionEmpresa(), c.getEstado(), c.getCreatedAt());
        }
    }

    public record LiquidacionResponse(Long id, Long conductorId, BigDecimal comisionAcumulada5,
                                      EstadoPago estadoPago, Instant createdAt, Instant fechaPago) {
        public static LiquidacionResponse of(Liquidacion l) {
            return new LiquidacionResponse(l.getId(), l.getConductor().getId(), l.getComisionAcumulada5(),
                    l.getEstadoPago(), l.getCreatedAt(), l.getFechaPago());
        }
    }

    public record DeudaConductorResponse(ConductorResponse conductor, BigDecimal totalPendiente,
                                         List<LiquidacionResponse> liquidaciones) {}
}
