package fr.tp.meteo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReponseMeteo(ConditionsActuelles current, Prevision daily) {
    public record ConditionsActuelles(
        @JsonProperty("temperature_2m") Double temperature,
        @JsonProperty("apparent_temperature") Double apparentTemperature,
        @JsonProperty("relative_humidity_2m") Integer humidity,
        Double precipitation,
        @JsonProperty("wind_speed_10m") Double windSpeed,
        @JsonProperty("weather_code") Integer weatherCode) {}
}
