package fr.tp.meteo.controller;
import fr.tp.meteo.model.Meteo;
import fr.tp.meteo.service.MeteoService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:4200")
public class MeteoRESTController {
    private final MeteoService meteoService;
    public MeteoRESTController(MeteoService meteoService) { this.meteoService = meteoService; }
    @GetMapping
    public Meteo getMeteo(@RequestParam(defaultValue = "") String city) {
        return meteoService.getMeteo(city);
    }
}
