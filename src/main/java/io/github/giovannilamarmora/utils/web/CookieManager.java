package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.math.MathService;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Logged
public class CookieManager {

  private static final Logger LOG = LoggerFactory.getLogger(MathService.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  private static void setCookieInResponse(
      String cookieName, String cookieValue, HttpServletResponse response) {
    LOG.info("Setting Cookie {}, with value {}", cookieName, cookieValue);
    ResponseCookie cookie =
        ResponseCookie.from(cookieName, cookieValue)
            .maxAge(360000)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    LOG.debug("Set Cookie {}, with value {}, successfully in Request", cookieName, cookieValue);
  }
}
