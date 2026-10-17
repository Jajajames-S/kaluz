package kaluz.demo.controller;

import jakarta.validation.Valid;
import kaluz.demo.dto.Requests.RegistroConductorRequest;
import kaluz.demo.dto.Responses.ConductorResponse;
import kaluz.demo.dto.Responses.RegistroConductorResponse;
import kaluz.demo.service.ConductorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
public class ConductorController {

    private final ConductorService service;

    public ConductorController(ConductorService service) {
        this.service = service;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroConductorResponse registro(@Valid @RequestBody RegistroConductorRequest req) {
        return service.registrar(req);
    }

    @GetMapping
    public List<ConductorResponse> listar() {
        return service.listar();
    }
}
