package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.util.Objects;
import org.slf4j.Logger;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

@Service
@Logged
public class CookieManager {

  private static final Logger LOG = LoggerFilter.getLogger(CookieManager.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static void setCookieInResponse(
      String cookieName, String cookieValue, ServerHttpResponse response) {
    LOG.debug("Setting Cookie {}, with value {}", cookieName, cookieValue);
    ResponseCookie cookie =
        ResponseCookie.from(cookieName, cookieValue)
            .maxAge(360000)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build();
    response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
    LOG.debug("Set Cookie {}, with value {}, successfully in Request", cookieName, cookieValue);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static ResponseCookie setCookie(String cookieName, String cookieValue) {
    LOG.debug("Setting Cookie {}, with value {}", cookieName, cookieValue);
    return ResponseCookie.from(cookieName, cookieValue)
        .maxAge(360000)
        .sameSite("None")
        .secure(true)
        .httpOnly(true)
        .path("/")
        .build();
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static HttpHeaders setCookieInResponse(String cookieName, String cookieValue) {
    LOG.debug("Setting Cookie {}, with value {}", cookieName, cookieValue);
    ResponseCookie cookie =
        ResponseCookie.from(cookieName, cookieValue)
            .maxAge(360000L)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build();
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    return headers;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String getCookie(String cookieName, ServerHttpRequest request) {
    LOG.debug("Getting Cookie {}", cookieName);
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    if (ObjectUtils.isEmpty(cookies) || ObjectUtils.isEmpty(cookies.get(cookieName))) return null;
    return Objects.requireNonNull(cookies.get(cookieName)).getFirst().getValue();
  }
}
