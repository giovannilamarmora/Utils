package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorsConfig implements WebFilter {

  private final Logger LOG = LoggerFilter.getLogger(this.getClass());

  // Enable CORS via environment variable
  @Value("#{new Boolean(${app.cors.enabled:false})}")
  private Boolean isCorsEnabled;

  // URL patterns that should not be filtered (e.g., Swagger UI and API docs)
  @Value("${app.cors.shouldNotFilter:**/swagger-ui/**,/api-docs,**/api-docs/**}")
  private List<String> shouldNotFilter;

  // Comma-separated allowed origins, e.g.,
  // "https://access.sphere.service.giovannilamarmora.com,https://linkatutto.giovannilamarmora.com,http://localhost:5501"
  @Value("${app.cors.allowedOrigins:*}")
  private String allowedOrigins;

  // Allowed headers (e.g., "Content-Type,Authorization")
  @Value("${app.cors.allowedHeaders:*}")
  private String allowedHeaders;

  // Flag to enable sending credentials (Access-Control-Allow-Credentials)
  @Value("${app.cors.allowCredentials:true}")
  private Boolean allowCredentials;

  // Parsed list of allowed origins
  private List<String> allowedOriginsList;

  @PostConstruct
  public void init() {
    allowedOriginsList = Arrays.asList(allowedOrigins.split(","));
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    if (isCorsEnabled) {
      // Get the Origin header from the request
      String requestOrigin = request.getHeaders().getOrigin();
      if (requestOrigin != null && allowedOriginsList.contains(requestOrigin)) {
        // Echo back the origin if it's in the allowed list
        exchange
            .getResponse()
            .getHeaders()
            .set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestOrigin);
      }
      // Set allowed methods
      exchange
          .getResponse()
          .getHeaders()
          .set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, PATCH, PUT, GET, OPTIONS, DELETE");
      // Set max age for preflight caching
      exchange.getResponse().getHeaders().set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
      // Set allowed headers
      exchange
          .getResponse()
          .getHeaders()
          .set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders);
      // If credentials are allowed, set the corresponding header
      if (allowCredentials) {
        exchange
            .getResponse()
            .getHeaders()
            .set(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
      }
      // Optionally, expose headers (you can adjust this as needed)
      exchange
          .getResponse()
          .getHeaders()
          .set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, allowedHeaders);

      LOG.info("CORS policy set for request from Origin: {}", requestOrigin);
    }
    return chain.filter(exchange);
  }

  /**
   * Determines if the current request should not be filtered by CORS. Excludes OPTIONS requests and
   * any request paths matching the specified patterns.
   */
  protected boolean shouldNotFilter(ServerHttpRequest req) {
    String path = req.getPath().value();
    String method = req.getMethod().name();
    // Exclude OPTIONS requests (commonly used for preflight)
    if (HttpMethod.OPTIONS.name().equals(method)) {
      return true;
    }
    // Check if the request path matches any excluded patterns
    return shouldNotFilter.stream()
        .anyMatch(endpoint -> PatternMatchUtils.simpleMatch(endpoint, path));
  }
}
