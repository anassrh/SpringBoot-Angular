package fr.tp.meteo.service;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
class MeteoServiceImplTest {
 @Test void rejectsEmptyCityBeforeCallingExternalApi() {
  var service = new MeteoServiceImpl(RestClient.builder());
  assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.getMeteo("  ")).getStatusCode().value());
  assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.getMeteo(null)).getStatusCode().value());
 }
}
