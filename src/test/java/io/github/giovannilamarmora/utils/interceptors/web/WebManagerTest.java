package io.github.giovannilamarmora.utils.interceptors.web;

import static org.junit.jupiter.api.Assertions.*;

import io.github.giovannilamarmora.utils.math.MathService;
import io.github.giovannilamarmora.utils.web.WebManager;
import java.net.URI;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriBuilder;

public class WebManagerTest {

  @Autowired private MathService mathService;

  @Test
  public void test_valid_url_string() {
    // Arrange
    Function<UriBuilder, URI> urlFunction = builder -> builder.path("/path").build();
    String baseUrl = "http://example.com";
    // Act
    String result = WebManager.getUrlAsString(urlFunction, baseUrl);
    // Assert
    assertEquals("http://example.com/path", result);
  }

  @Test
  public void test_empty_string() {
    // Arrange
    Function<UriBuilder, URI> urlFunction = null;
    String baseUrl = null;
    // Act
    String result = WebManager.getUrlAsString(urlFunction, baseUrl);
    // Assert
    assertNull(result);
  }
}
