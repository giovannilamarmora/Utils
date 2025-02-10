package io.github.giovannilamarmora.utils.context;

import static io.github.giovannilamarmora.utils.context.ContextConfig.*;

import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.logger.MDCUtils;
import io.github.giovannilamarmora.utils.web.ResponseManager;
import io.github.giovannilamarmora.utils.web.WebManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
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

  @Value(value = "${spring.application.name:no_name_defined}")
  private String application_name;

  @Value(value = "${app.version:no_version_defined}")
  private String app_version;

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

    String traceId = getOrGenerateId(request, TRACE_ID_PATTERN, TRACE_ID.getValue());
    String spanId = TraceUtils.generateTrace();
    String parentId = getOrGenerateId(request, SPAN_ID_PATTERN, SPAN_ID.getValue());

    ResponseManager.setCookieAndHeaderData(TRACE_ID.getValue(), traceId, response);
    ResponseManager.setCookieAndHeaderData(SPAN_ID.getValue(), spanId, response);
    ResponseManager.setCookieAndHeaderData(PARENT_ID.getValue(), parentId, response);

    return Mono.fromRunnable(
            () -> {
              MDCUtils.setDataIntoMDC(TRACE_ID.getValue(), traceId);
              MDCUtils.setDataIntoMDC(SPAN_ID.getValue(), spanId);
              MDCUtils.setDataIntoMDC(PARENT_ID.getValue(), parentId);
              MDCUtils.setDataIntoMDC(ENV.getValue(), env);
              MDCUtils.setDataIntoMDC(APP_NAME.getValue(), application_name);
              MDCUtils.setDataIntoMDC(APP_VERSION.getValue(), app_version);
            })
        .then(
            chain
                .filter(exchange)
                .contextWrite(
                    context -> {
                      Map<String, String> contextMap = new HashMap<>();
                      contextMap.put(TRACE_ID.getValue(), traceId);
                      contextMap.put(SPAN_ID.getValue(), spanId);
                      contextMap.put(PARENT_ID.getValue(), parentId);
                      contextMap.put(ENV.getValue(), env);
                      contextMap.put(APP_NAME.getValue(), application_name);
                      contextMap.put(APP_VERSION.getValue(), app_version);
                      return Context.of(contextMap);
                    }))
        .doFinally(signalType -> MDC.clear());
  }

  private String getOrGenerateId(
      ServerHttpRequest request, Pattern pattern, String defaultHeaderName) {
    return getCookieValue(request, defaultHeaderName)
        .orElseGet(
            () ->
                getHeaderValue(request.getHeaders(), pattern).orElseGet(TraceUtils::generateTrace));
  }

  private Optional<String> getCookieValue(ServerHttpRequest request, String cookieName) {
    HttpCookie cookie = request.getCookies().getFirst(cookieName);
    return !ObjectUtils.isEmpty(cookie) ? Optional.of(cookie.getValue()) : Optional.empty();
  }

  private Optional<String> getHeaderValue(HttpHeaders headers, Pattern pattern) {
    return headers.entrySet().stream()
        .filter(entry -> pattern.matcher(entry.getKey()).matches())
        .findFirst()
        .map(entry -> entry.getValue().getFirst());
  }
}
