package io.github.giovannilamarmora.utils.interceptors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class UtilsInterceptor implements WebFilter {

  private static final String MDC_ENV = "environment";

  @Value(value = "${env:Default}")
  private String env;

  private static boolean isEmpty(String value) {
    return value == null || value.isBlank();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String mdcEnv = MDC.get(MDC_ENV);
    if (isEmpty(mdcEnv) || !mdcEnv.equalsIgnoreCase(env)) {
      MDC.remove(MDC_ENV);
    }
    MDC.put(MDC_ENV, env);
    return chain.filter(exchange);
  }
}
