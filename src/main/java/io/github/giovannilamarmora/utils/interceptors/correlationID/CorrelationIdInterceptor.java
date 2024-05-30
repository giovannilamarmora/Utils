package io.github.giovannilamarmora.utils.interceptors.correlationID;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdInterceptor implements WebFilter {

  private static boolean isEmpty(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (shouldNotFilter(exchange.getRequest())) return chain.filter(exchange);
    String correlationId =
        exchange.getRequest().getHeaders().getFirst(CorrelationIdUtils.CORRELATION_HEADER_NAME);
    String mdcCorrelationId = MDC.get(CorrelationIdUtils.CORRELATION_MDC_NAME);
    if (isEmpty(correlationId) || !correlationId.equalsIgnoreCase(mdcCorrelationId)) {
      MDC.remove(CorrelationIdUtils.CORRELATION_MDC_NAME);
      correlationId = CorrelationIdUtils.generateCorrelationId();
    }
    CorrelationIdUtils.setCorrelationId(correlationId);
    MDC.put(CorrelationIdUtils.CORRELATION_MDC_NAME, CorrelationIdUtils.getCorrelationId());
    exchange
        .getResponse()
        .getHeaders()
        .add(CorrelationIdUtils.CORRELATION_HEADER_NAME, CorrelationIdUtils.getCorrelationId());
    return chain.filter(exchange);
  }

  protected boolean shouldNotFilter(ServerHttpRequest request) {
    String path = request.getPath().value();
    return "/actuator/health".equals(path);
  }
}
