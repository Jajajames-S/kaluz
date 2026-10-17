package kaluz.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import kaluz.demo.dto.Requests.GpsRequest;
import kaluz.demo.dto.Responses.VehiculoCercanoResponse;
import kaluz.demo.dto.Responses.VehiculoResponse;
import kaluz.demo.model.TipoVehiculo;
import kaluz.demo.service.VehiculoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService service;

    public VehiculoController(VehiculoService service) {
        this.service = service;
    }

    @PutMapping("/{id}/gps")
    public VehiculoResponse gps(@PathVariable Long id, @Valid @RequestBody GpsRequest req) {
        return service.actualizarGps(id, req);
    }

    /** Ej: GET /api/vehiculos/cercanos?lat=-12.0651&lng=-75.2049&tipo=TAXI */
    @GetMapping("/cercanos")
    public VehiculoCercanoResponse cercanos(
            @RequestParam @DecimalMin("-90") @DecimalMax("90") double lat,
            @RequestParam @DecimalMin("-180") @DecimalMax("180") double lng,
            @RequestParam(required = false) TipoVehiculo tipo) {
        return service.masCercano(lat, lng, tipo);
    }

    @GetMapping
    public List<VehiculoResponse> listar() {
        return service.listar();
    }
}
