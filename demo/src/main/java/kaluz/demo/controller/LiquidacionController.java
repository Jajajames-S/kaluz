package kaluz.demo.controller;

import kaluz.demo.dto.Responses.DeudaConductorResponse;
import kaluz.demo.dto.Responses.LiquidacionResponse;
import kaluz.demo.service.LiquidacionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/liquidaciones")
public class LiquidacionController {

    private final LiquidacionService service;

    public LiquidacionController(LiquidacionService service) {
        this.service = service;
    }

    @GetMapping("/conductor/{id}")
    public DeudaConductorResponse deuda(@PathVariable Long id) {
        return service.deudaDeConductor(id);
    }

    @PutMapping("/{id}/pagar")
    public LiquidacionResponse pagar(@PathVariable Long id) {
        return service.pagar(id);
    }
}
