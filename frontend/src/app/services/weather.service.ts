import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Weather } from '../models/weather';

@Injectable({ providedIn: 'root' })
export class WeatherService {
  // Le backend a été lancé sur 8081 car 8080 était occupé.
  private readonly apiUrl = 'http://localhost:8081/api/weather';

  constructor(private readonly http: HttpClient) {}

  getWeather(city: string): Observable<Weather> {
    // HttpClient encode le paramètre : espaces, accents, etc.
    return this.http.get<Weather>(this.apiUrl, { params: { city } });
  }
}
