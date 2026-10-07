package fr.tp.meteo.model;
// Réponse simple destinée à Angular. Les valeurs quotidiennes concernent aujourd'hui.
public record Meteo(String city, String country, double latitude, double longitude,
    double temperature, double apparentTemperature, int humidity, double precipitation,
    double windSpeed, int weatherCode, int dailyWeatherCode, double temperatureMax,
    double temperatureMin, int precipitationProbabilityMax) {}
