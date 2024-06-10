package io.github.giovannilamarmora.utils.interceptors.correlationID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(value = 1)
@Component
public class CorrelationIdInterceptor implements WebFilter {

  private static final String MDC_ENV = "environment";

  @Value(value = "${env:Default}")
  private String env;

  private static boolean isEmpty(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (shouldNotFilter(exchange.getRequest())) return chain.filter(exchange);

    String correlationId =
        exchange.getRequest().getHeaders().getFirst(CorrelationIdUtils.CORRELATION_HEADER_NAME);
    if (isEmpty(correlationId)) {
      correlationId = CorrelationIdUtils.generateCorrelationId();
    }

    CorrelationIdUtils.setCorrelationId(correlationId);
    exchange
        .getResponse()
        .getHeaders()
        .add(CorrelationIdUtils.CORRELATION_HEADER_NAME, correlationId);

    MDC.put(CorrelationIdUtils.CORRELATION_MDC_NAME, correlationId);
    MDC.put(MDC_ENV, env);

    return chain
        .filter(exchange)
        .doFinally(
            signalType -> {
              MDC.remove(CorrelationIdUtils.CORRELATION_MDC_NAME);
              MDC.remove(MDC_ENV);
            });
  }

  protected boolean shouldNotFilter(ServerHttpRequest request) {
    String path = request.getPath().value();
    return "/actuator/health".equals(path);
  }
}
