// Cette interface correspond au JSON retourné par Spring Boot.
export interface Weather {
  city: string;
  country: string | null;
  latitude: number;
  longitude: number;
  temperature: number;
  apparentTemperature: number;
  humidity: number;
  precipitation: number;
  windSpeed: number;
  weatherCode: number;
  dailyWeatherCode: number;
  temperatureMax: number;
  temperatureMin: number;
  precipitationProbabilityMax: number;
}
