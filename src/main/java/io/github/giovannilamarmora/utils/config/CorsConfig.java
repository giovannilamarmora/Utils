package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.util.List;
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

  @Value(value = "#{new Boolean(${app.cors.enabled:false})}")
  private Boolean isCorsEnabled;

  @Value(value = "${app.shouldNotFilter}")
  private List<String> shouldNotFilter;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    if (isCorsEnabled && !shouldNotFilter(request)) {
      exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
      exchange
          .getResponse()
          .getHeaders()
          .add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, PATCH, PUT, GET, OPTIONS, DELETE");
      exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
      exchange.getResponse().getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
      exchange
          .getResponse()
          .getHeaders()
          .add(
              HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
              "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      LOG.info("Setting Up CORS Policy for mainstream: {}", exchange.getResponse());
    }
    return chain.filter(exchange);
  }

  protected boolean shouldNotFilter(ServerHttpRequest req) {
    String path = req.getPath().value();
    String method = req.getMethod().name();
    if (HttpMethod.OPTIONS.name().equals(method)) {
      return true;
    }
    return shouldNotFilter.stream()
        .anyMatch(endpoint -> PatternMatchUtils.simpleMatch(endpoint, path));
  }
}
