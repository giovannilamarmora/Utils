package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

@Service
@Logged
public class WebManager {

  private static final Logger LOG = LoggerFilter.getLogger(WebManager.class);
  private static final String CLIENT_IP = "Client-IP";
  private static final String X_FORWARDED_FOR = "X-Forwarded-For";
  private static final String X_ORIGINAL_FORWARDED_FOR = "x-original-forwarded-for";

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String getRealClientIP(ServerHttpRequest request) {
    LOG.debug("Getting Real Client IP for {}", request.getPath().value());
    String ipClient = null;
    String headerClientIp = request.getHeaders().getFirst(CLIENT_IP);
    String headerXForwardedFor = request.getHeaders().getFirst(X_FORWARDED_FOR);
    String headerOriginalXForwardedFor = request.getHeaders().getFirst(X_ORIGINAL_FORWARDED_FOR);
    LOG.info("Info header Client-IP: " + headerClientIp);
    LOG.info("Info header X-Forwarded-For: " + headerXForwardedFor);
    LOG.info("Info header x-original-forwarded-for: " + headerOriginalXForwardedFor);

    if (headerClientIp != null && !headerClientIp.isEmpty()) {
      ipClient = headerClientIp;
    } else if (headerXForwardedFor != null && !headerXForwardedFor.isEmpty()) {
      ipClient = headerXForwardedFor;
    } else if (headerOriginalXForwardedFor != null && !headerOriginalXForwardedFor.isEmpty()) {
      ipClient = getClientIp(request, headerOriginalXForwardedFor);
    }
    if (ipClient == null) {
      ipClient = Objects.requireNonNull(request.getRemoteAddress()).getHostName();
    }
    LOG.debug("Ended Get Real Client IP: {}", ipClient);
    return ipClient;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String getClientIp(ServerHttpRequest request, String xForwardedForHeader) {
    LOG.debug("Getting Client IP for {}", xForwardedForHeader);
    if (xForwardedForHeader == null) {
      return Objects.requireNonNull(request.getRemoteAddress()).getHostName();
    } else {
      return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String getUrlAsString(Function<UriBuilder, URI> urlFunction, String baseUrl) {
    if (ObjectUtils.isEmpty(urlFunction)) return null;
    if (ObjectUtils.isEmpty(baseUrl)) baseUrl = "";
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromUri(
            urlFunction.apply(UriComponentsBuilder.fromUriString(baseUrl)));
    return UriUtils.decode(uriComponentsBuilder.build().toUriString(), StandardCharsets.UTF_8);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String getRemoteAddress(ServerHttpRequest request) {
    return ObjectUtils.isEmpty(request.getRemoteAddress())
        ? null
        : request.getRemoteAddress().getHostName();
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static boolean shouldNotFilter(ServerHttpRequest req, List<String> shouldNotFilter) {
    String path = req.getPath().value();
    String method = req.getMethod().name();
    if (HttpMethod.OPTIONS.name().equals(method)) {
      return true;
    }
    return shouldNotFilter.stream()
        .anyMatch(endpoint -> PatternMatchUtils.simpleMatch(endpoint, path));
  }
}
