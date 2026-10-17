package kaluz.demo.controller;

import jakarta.validation.Valid;
import kaluz.demo.dto.Requests.DocumentoCreateRequest;
import kaluz.demo.dto.Responses.DocumentoResponse;
import kaluz.demo.service.DocumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController(DocumentoService service) {
        this.service = service;
    }

    @GetMapping("/alertas")
    public List<DocumentoResponse> alertas() {
        return service.alertas();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoResponse crear(@Valid @RequestBody DocumentoCreateRequest req) {
        return service.crear(req);
    }
}
