package fr.tp.meteo.service;
import fr.tp.meteo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class MeteoServiceImpl implements MeteoService {
    private final RestClient client;
    public MeteoServiceImpl(RestClient.Builder builder) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        client = builder.requestFactory(factory).build();
    }
    @Override
    public Meteo getMeteo(String ville) {
        if (ville == null || ville.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez saisir une ville.");
        }
        try {
            ReponseVille geocoding = client.get().uri(builder -> builder
                .scheme("https").host("geocoding-api.open-meteo.com").path("/v1/search")
                .queryParam("name", "{city}").queryParam("count", 1)
                .queryParam("language", "fr").queryParam("format", "json")
                .build(ville.trim())).retrieve().body(ReponseVille.class);
            if (geocoding == null || geocoding.results() == null || geocoding.results().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ville introuvable.");
            }
            var location = geocoding.results().get(0);
            if (location.latitude() == null || location.longitude() == null) throw incomplete();
            ReponseMeteo forecast = client.get().uri(builder -> builder
                .scheme("https").host("api.open-meteo.com").path("/v1/forecast")
                .queryParam("latitude", location.latitude()).queryParam("longitude", location.longitude())
                .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code")
                .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
                .queryParam("timezone", "auto").queryParam("forecast_days", 1)
                .queryParam("temperature_unit", "celsius").queryParam("wind_speed_unit", "kmh")
                .queryParam("precipitation_unit", "mm").build()).retrieve().body(ReponseMeteo.class);
            if (forecast == null || forecast.current() == null || forecast.daily() == null) throw incomplete();
            var current = forecast.current();
            var daily = forecast.daily();
            if (current.temperature() == null || current.apparentTemperature() == null || current.humidity() == null
                || current.precipitation() == null || current.windSpeed() == null || current.weatherCode() == null) throw incomplete();
            return new Meteo(location.name(), location.country(), location.latitude(), location.longitude(),
                current.temperature(), current.apparentTemperature(), current.humidity(), current.precipitation(),
                current.windSpeed(), current.weatherCode(), first(daily.weatherCode()), first(daily.temperatureMax()),
                first(daily.temperatureMin()), first(daily.precipitationProbabilityMax()));
        } catch (RestClientException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Impossible de contacter Open-Meteo. Réessayez plus tard.");
        }
    }
    private <T> T first(List<T> values) {
        if (values == null || values.isEmpty() || values.get(0) == null) throw incomplete();
        return values.get(0);
    }
    private ResponseStatusException incomplete() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Données Open-Meteo incomplètes.");
    }
}
