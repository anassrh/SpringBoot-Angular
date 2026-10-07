package fr.tp.meteo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Prevision(
        @JsonProperty("weather_code") List<Integer> weatherCode,
        @JsonProperty("temperature_2m_max") List<Double> temperatureMax,
        @JsonProperty("temperature_2m_min") List<Double> temperatureMin,
        @JsonProperty("precipitation_probability_max") List<Integer> precipitationProbabilityMax) {}
