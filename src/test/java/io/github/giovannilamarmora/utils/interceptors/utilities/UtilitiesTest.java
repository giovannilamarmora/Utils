package io.github.giovannilamarmora.utils.interceptors.utilities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.giovannilamarmora.utils.utilities.Utilities;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

public class UtilitiesTest {

  @InjectMocks private Utilities utilities;

  @Test
  void testConvertObjectToJson() throws JsonProcessingException {
    // Simula un oggetto da convertire in JSON
    Object testObject = new Object();

    // Chiamata al metodo da testare
    String jsonResult = Utilities.convertObjectToJson(testObject);

    // Verifica che il risultato non sia nullo
    assertNotNull(jsonResult);
  }

  @Test
  void testConvertObjectToJsonWithNullObject() throws JsonProcessingException {
    // Chiamata al metodo da testare con un oggetto nullo
    String jsonResult = Utilities.convertObjectToJson(null);

    // Verifica che il risultato sia nullo
    assertNull(jsonResult);
  }

  @Test
  void testConvertMapToString() {
    // Simula una mappa da convertire in stringa
    Map<String, Object> testMap = new HashMap<>();
    testMap.put("key1", "value1");
    testMap.put("key2", "value2");

    // Chiamata al metodo da testare
    String mapString = Utilities.convertMapToString(testMap);

    // Verifica che il risultato non sia nullo
    assertNotNull(mapString);
  }

  @Test
  void testConvertMapToStringWithNullMap() {
    // Chiamata al metodo da testare con una mappa nulla
    String mapString = Utilities.convertMapToString(null);

    // Verifica che il risultato sia nullo
    assertNull(mapString);
  }

  @Test
  void testGetByteArrayFromImageURL() throws IOException {
    // Simula un'URL di immagine
    String imageUrl = "https://example.com/image.jpg";

    // Simula l'oggetto URLConnection e l'InputStream
    URLConnection urlConnection = mock(URLConnection.class);
    InputStream inputStream = new ByteArrayInputStream(new byte[] {1, 2, 3});

    // Utilizza Mockito per simulare il comportamento della connessione
    when(urlConnection.getInputStream()).thenReturn(inputStream);

    // Chiamata al metodo da testare
    String result = Utilities.getByteArrayFromImageURL(imageUrl);

    // Verifica che il risultato non sia nullo
    assertNotNull(result);
  }

  @Test
  void testGetByteArrayFromImageURLWithInvalidURL() {
    // Chiamata al metodo da testare con un'URL non valida
    String result = Utilities.getByteArrayFromImageURL("invalid-url");

    // Verifica che il risultato sia la stessa URL
    assertEquals("invalid-url", result);
  }
}
