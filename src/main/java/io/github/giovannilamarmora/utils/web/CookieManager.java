package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
@Logged
public class CookieManager {

  private static final Logger LOG = LoggerFactory.getLogger(CookieManager.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static void setCookieInResponse(
      String cookieName, String cookieValue, ServerHttpRequest response) {
    LOG.info("Setting Cookie {}, with value {}", cookieName, cookieValue);
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
}
