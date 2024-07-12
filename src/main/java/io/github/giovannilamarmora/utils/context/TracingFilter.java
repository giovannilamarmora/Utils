package io.github.giovannilamarmora.utils.context;

import static io.github.giovannilamarmora.utils.context.ContextConfig.*;

import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.web.WebManager;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TracingFilter implements WebFilter {

  @Value(value = "${env:Default}")
  private String env;

  private static final Logger LOG = LoggerFilter.getLogger(TracingFilter.class);

  private static final Pattern TRACE_ID_PATTERN =
      Pattern.compile(".*trace-id.*", Pattern.CASE_INSENSITIVE);
  private static final Pattern SPAN_ID_PATTERN =
      Pattern.compile(".*span-id.*", Pattern.CASE_INSENSITIVE);
  private static final Pattern PARENT_ID_PATTERN =
      Pattern.compile(".*parent-id.*", Pattern.CASE_INSENSITIVE);

  private final List<String> shouldNotFilter = List.of("/actuator/health");

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    ServerHttpResponse response = exchange.getResponse();
    if (WebManager.shouldNotFilter(request, shouldNotFilter)) {
      return chain.filter(exchange);
    }

    HttpHeaders requestHeaders = request.getHeaders();
    HttpHeaders responseHeaders = response.getHeaders();

    String traceId = getOrGenerateId(requestHeaders, TRACE_ID_PATTERN, TRACE_ID.getValue());
    String spanId = TraceUtils.generateTrace();
    String parentId = getOrGenerateId(requestHeaders, SPAN_ID_PATTERN, SPAN_ID.getValue());

    responseHeaders.set(TRACE_ID.getValue(), traceId);
    responseHeaders.set(SPAN_ID.getValue(), spanId);
    responseHeaders.set(PARENT_ID.getValue(), parentId);

    return Mono.fromRunnable(
            () -> {
              MDC.put(TRACE_ID.getValue(), traceId);
              MDC.put(SPAN_ID.getValue(), spanId);
              MDC.put(PARENT_ID.getValue(), parentId);
              if (!ObjectUtils.isEmpty(env)) MDC.put(ENV.getValue(), env);
            })
        .then(
            chain
                .filter(exchange)
                .contextWrite(
                    Context.of(
                        TRACE_ID.getValue(),
                        traceId,
                        SPAN_ID.getValue(),
                        spanId,
                        PARENT_ID.getValue(),
                        parentId,
                        ENV.getValue(),
                        env)))
        .doFinally(signalType -> MDC.clear());
  }

  private String getOrGenerateId(HttpHeaders headers, Pattern pattern, String defaultHeaderName) {
    return headers.entrySet().stream()
        .filter(entry -> pattern.matcher(entry.getKey()).matches())
        .findFirst()
        .map(entry -> entry.getValue().getFirst())
        .orElseGet(
            () -> {
              String generatedId = TraceUtils.generateTrace();
              LOG.debug("Generated new {} : {}", defaultHeaderName, generatedId);
              return generatedId;
            });
  }
}
