package kaluz.demo.service;

import kaluz.demo.dto.Requests.GpsRequest;
import kaluz.demo.dto.Responses.ConductorResponse;
import kaluz.demo.dto.Responses.VehiculoCercanoResponse;
import kaluz.demo.dto.Responses.VehiculoResponse;
import kaluz.demo.exception.NotFoundException;
import kaluz.demo.model.EstadoConductor;
import kaluz.demo.model.TipoVehiculo;
import kaluz.demo.model.Vehiculo;
import kaluz.demo.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class VehiculoService {

    private static final double RADIO_TIERRA_KM = 6371.0088;

    private final VehiculoRepository vehiculos;

    public VehiculoService(VehiculoRepository vehiculos) {
        this.vehiculos = vehiculos;
    }

    @Transactional
    public VehiculoResponse actualizarGps(Long id, GpsRequest req) {
        Vehiculo v = vehiculos.findById(id)
                .orElseThrow(() -> new NotFoundException("Vehiculo " + id + " no existe"));
        v.setLatitud(req.latitud());
        v.setLongitud(req.longitud());
        v.setUltimaActualizacionGps(Instant.now());
        return VehiculoResponse.of(v);
    }

    /** Vehiculo con chofer ACTIVO mas cercano al punto indicado (distancia haversine). */
    @Transactional(readOnly = true)
    public VehiculoCercanoResponse masCercano(double lat, double lng, TipoVehiculo tipo) {
        return vehiculos.findByConductor_EstadoAndLatitudIsNotNullAndLongitudIsNotNull(EstadoConductor.ACTIVO)
                .stream()
                .filter(v -> tipo == null || v.getTipo() == tipo)
                .map(v -> new VehiculoCercanoResponse(VehiculoResponse.of(v), ConductorResponse.of(v.getConductor()),
                        redondear(haversineKm(lat, lng, v.getLatitud(), v.getLongitud()))))
                .min(Comparator.comparingDouble(VehiculoCercanoResponse::distanciaKm))
                .orElseThrow(() -> new NotFoundException("No hay vehiculos activos con GPS disponibles"));
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listar() {
        return vehiculos.findAll().stream().map(VehiculoResponse::of).toList();
    }

    static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(a));
    }

    private static double redondear(double km) {
        return Math.round(km * 1000.0) / 1000.0;
    }
}
