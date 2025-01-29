package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.Mapper;
import io.github.giovannilamarmora.utils.utilities.Utilities;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public interface WebManager {

  Logger LOG = LoggerFilter.getLogger(WebManager.class);
  String CLIENT_IP = "Client-IP";
  String X_FORWARDED_FOR = "X-Forwarded-For";
  String X_ORIGINAL_FORWARDED_FOR = "x-original-forwarded-for";

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getAddressFromRequest(ServerHttpRequest request) {
    List<String> host = request.getHeaders().get("Referer");
    if (ObjectUtils.isEmpty(host)) return WebManager.getRealClientIP(request);
    Map<String, Object> requestData = new HashMap<>();
    requestData.put("ingress_host", host);
    requestData.put("ip_address", WebManager.getRealClientIP(request));
    return Mapper.writeObjectToString(requestData);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getHostFromRequest(ServerHttpRequest request) {
    List<String> host = request.getHeaders().get("Referer");
    return Mapper.writeObjectToString(host);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getRealClientIP(ServerHttpRequest request) {
    LOG.debug("Getting Real Client IP for {}", request.getPath().value());
    String ipClient = null;
    String headerClientIp = request.getHeaders().getFirst(CLIENT_IP);
    String headerXForwardedFor = request.getHeaders().getFirst(X_FORWARDED_FOR);
    String headerOriginalXForwardedFor = request.getHeaders().getFirst(X_ORIGINAL_FORWARDED_FOR);
    LOG.debug("Info header Client-IP: {}", headerClientIp);
    LOG.debug("Info header X-Forwarded-For: {}", headerXForwardedFor);
    LOG.debug("Info header x-original-forwarded-for: {}", headerOriginalXForwardedFor);

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
  private static String getClientIp(ServerHttpRequest request, String xForwardedForHeader) {
    LOG.debug("Getting Client IP for {}", xForwardedForHeader);
    if (xForwardedForHeader == null) {
      return Objects.requireNonNull(request.getRemoteAddress()).getHostName();
    } else {
      return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getUrlAsString(Function<UriBuilder, URI> urlFunction, String baseUrl) {
    if (ObjectUtils.isEmpty(urlFunction)) return null;
    if (ObjectUtils.isEmpty(baseUrl)) baseUrl = "";
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromUri(
            urlFunction.apply(UriComponentsBuilder.fromUriString(baseUrl)));
    return UriUtils.decode(uriComponentsBuilder.build().toUriString(), StandardCharsets.UTF_8);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getRemoteAddress(ServerHttpRequest request) {
    String clientIp = getRealClientIP(request);
    if (Utilities.isNullOrEmpty(clientIp)) {
      if (!Utilities.isNullOrEmpty(request.getRemoteAddress()))
        clientIp = request.getRemoteAddress().getHostName();
      else return null;
    }

    try {
      InetAddress inetAddress = InetAddress.getByName(clientIp);

      if (!Utilities.isNullOrEmpty(inetAddress.getHostName())) return inetAddress.getHostName();
    } catch (Exception e) {
      if (!Utilities.isNullOrEmpty(request.getRemoteAddress()))
        return request.getRemoteAddress().getHostName();
      else return null;
    }
    return clientIp;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static boolean shouldNotFilter(ServerHttpRequest req, List<String> shouldNotFilter) {
    String path = req.getPath().value();
    String method = req.getMethod().name();
    if (HttpMethod.OPTIONS.name().equals(method)) {
      return true;
    }
    return shouldNotFilter.stream()
        .anyMatch(endpoint -> PatternMatchUtils.simpleMatch(endpoint, path));
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String encodeURLValue(String value) {
    return ObjectUtils.isEmpty(value) ? value : URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String decodeURLValue(String value) {
    return ObjectUtils.isEmpty(value) ? value : URLDecoder.decode(value, StandardCharsets.UTF_8);
  }
}
