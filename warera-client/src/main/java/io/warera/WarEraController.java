package io.warera;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.warera.service.WarEraService;

@RestController
@RequestMapping("/run")
public class WarEraController {

    private final WarEraService warEraService;

    public WarEraController(WarEraService warEraService) {
        this.warEraService = warEraService;
    }

    @GetMapping("/profitPerHour")
    public Map<String, Double> run() throws Exception {
        return warEraService.profitPerHour();
    }
    @GetMapping("/profitPerPoint")
    public Map<String,Double> profitPerPoint() throws Exception{
        return warEraService.profitPerPoint();
    }
}