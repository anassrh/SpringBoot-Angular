import { Component } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { Weather } from '../models/weather';
import { WeatherService } from '../services/weather.service';

@Component({
  selector: 'app-weather',
  standalone: true,
  imports: [FormsModule, DecimalPipe],
  templateUrl: './weather.component.html',
  styleUrl: './weather.component.css'
})
export class WeatherComponent {
  city = 'Paris';
  weather: Weather | null = null;
  loading = false;
  errorMessage = '';

  constructor(private readonly weatherService: WeatherService) {}

  search(): void {
    if (this.loading) return;
    this.errorMessage = '';
    this.weather = null;
    const city = this.city.trim();
    if (!city) {
      this.errorMessage = 'Veuillez saisir une ville.';
      return;
    }
    this.loading = true;
    this.weatherService.getWeather(city)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: weather => this.weather = weather,
        error: (error: HttpErrorResponse) => {
          if (error.status === 0) {
            this.errorMessage = 'Impossible de joindre le serveur météo. Vérifiez que Spring Boot est lancé sur le port 8081.';
          } else {
            this.errorMessage = error.error?.detail || 'La météo est indisponible. Réessayez plus tard.';
          }
        }
      });
  }

  weatherLabel(code: number): string {
    if (code === 0) return 'Ciel dégagé';
    if (code === 1) return 'Principalement dégagé';
    if (code === 2) return 'Partiellement nuageux';
    if (code === 3) return 'Couvert';
    if ([45, 48].includes(code)) return 'Brouillard';
    if ([51, 53, 55, 56, 57].includes(code)) return 'Bruine';
    if ([61, 63, 65, 66, 67].includes(code)) return 'Pluie';
    if ([71, 73, 75, 77].includes(code)) return 'Neige';
    if ([80, 81, 82].includes(code)) return 'Averses';
    if ([85, 86].includes(code)) return 'Averses de neige';
    if ([95, 96, 99].includes(code)) return 'Orage';
    return 'Conditions inconnues';
  }
}
