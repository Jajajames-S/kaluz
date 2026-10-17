package kaluz.demo.controller;

import jakarta.validation.Valid;
import kaluz.demo.dto.Requests.CarreraRequest;
import kaluz.demo.dto.Responses.CarreraResponse;
import kaluz.demo.service.CarreraService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraService service;

    public CarreraController(CarreraService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarreraResponse registrar(@Valid @RequestBody CarreraRequest req) {
        return service.registrar(req);
    }
}
