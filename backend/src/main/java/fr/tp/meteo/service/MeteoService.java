package fr.tp.meteo.service;

import fr.tp.meteo.model.Meteo;

public interface MeteoService {
    Meteo getMeteo(String ville);
}
