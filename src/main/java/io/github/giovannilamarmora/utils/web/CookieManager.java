package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.time.Duration;
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
public interface CookieManager {

  Logger LOG = LoggerFilter.getLogger(CookieManager.class);

  /**
   * Sets a cookie in the HTTP response with security settings and domain handling.
   *
   * <p>This method ensures that cookies are securely stored and accessible only where needed. The
   * domain for the cookie is determined dynamically based on the request headers.
   *
   * <h3>Cookie Attributes:</h3>
   *
   * <ul>
   *   <li><b>Secure:</b> Ensures cookies are sent only over HTTPS.
   *   <li><b>HttpOnly:</b> Prevents JavaScript from accessing the cookie (XSS protection).
   *   <li><b>SameSite=None:</b> Allows cross-origin usage (needed for authentication flows).
   *   <li><b>Path=/:</b> Makes the cookie accessible across the entire subdomain.
   *   <li><b>Max-Age=360000:</b> Defines the cookie’s expiration time.
   * </ul>
   *
   * <h3>Domain Handling:</h3>
   *
   * <ul>
   *   <li>The domain is extracted from the {@code Referer} or {@code Origin} header.
   *   <li>If neither is available, the cookie remains bound to the authentication service’s domain.
   * </ul>
   *
   * @param cookieName The name of the cookie.
   * @param cookieValue The value to store in the cookie.
   * @param response The {@link ServerHttpResponse} object where the cookie will be set.
   * @param request The {@link ServerHttpRequest} object used to determine the cookie domain.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void setCookieInResponse(
      String cookieName,
      String cookieValue,
      ServerHttpResponse response,
      ServerHttpRequest request) {

    String redirectUri = request.getQueryParams().getFirst("redirect_uri");
    // Extract domain from Referer or Origin (keeping URL handling as before)
    String referer = request.getHeaders().getFirst(HttpHeaders.REFERER);
    String origin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
    String host = request.getHeaders().getFirst(HttpHeaders.HOST);
    String url =
        (redirectUri != null)
            ? redirectUri
            : (origin != null && !origin.equals("*") ? origin : (host != null ? host : referer));
    String domain = WebManager.extractDomain(url);

    // Create the cookie with security attributes
    ResponseCookie.ResponseCookieBuilder cookieBuilder =
        ResponseCookie.from(cookieName, cookieValue)
            .maxAge(360000)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .path("/");

    // Set domain if available
    if (domain != null) {
      cookieBuilder.domain(domain);
    }

    // Add cookie to response
    response.getHeaders().add(HttpHeaders.SET_COOKIE, cookieBuilder.build().toString());

    LOG.debug("Set Cookie {}, with value {}, successfully in Response", cookieName, cookieValue);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void setCookieInResponse(
      String cookieName, String cookieValue, ServerHttpResponse response) {
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
  static void setCookieInResponse(
      String cookieName, String cookieValue, String domain, ServerHttpResponse response) {
    ResponseCookie cookie =
        ResponseCookie.from(cookieName, cookieValue)
            .domain(domain)
            .maxAge(360000)
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .path("/")
            .build();
    response.getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static ResponseCookie setCookie(String cookieName, String cookieValue) {
    return ResponseCookie.from(cookieName, cookieValue)
        .maxAge(360000)
        .sameSite("None")
        .secure(true)
        .httpOnly(true)
        .path("/")
        .build();
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static ResponseCookie setCookie(String cookieName, String cookieValue, String domain) {
    return ResponseCookie.from(cookieName, cookieValue)
        .domain(domain)
        .maxAge(360000)
        .sameSite("None")
        .secure(true)
        .httpOnly(true)
        .path("/")
        .build();
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static HttpHeaders setCookieInResponse(String cookieName, String cookieValue) {
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
  static HttpHeaders setCookieInResponse(String cookieName, String cookieValue, String domain) {
    ResponseCookie cookie =
        ResponseCookie.from(cookieName, cookieValue)
            .domain(domain)
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
  static String getCookie(String cookieName, ServerHttpRequest request) {
    LOG.debug("Getting Cookie {}", cookieName);
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();
    if (ObjectUtils.isEmpty(cookies) || ObjectUtils.isEmpty(cookies.get(cookieName))) return null;
    return Objects.requireNonNull(cookies.get(cookieName)).getFirst().getValue();
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void deleteCookie(String cookieName, ServerHttpResponse response) {
    LOG.debug("Deleting Cookie {}", cookieName);

    // Creazione di un cookie con lo stesso nome e un tempo di scadenza nel passato
    ResponseCookie cookieToDelete =
        ResponseCookie.from(cookieName, "").maxAge(Duration.ofSeconds(0)).path("/").build();

    // Aggiungi il cookie di cancellazione alla risposta
    response.addCookie(cookieToDelete);

    LOG.debug("Cookie {} deleted", cookieName);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void deleteCookie(
      String cookieName, ServerHttpRequest request, ServerHttpResponse response) {
    LOG.debug("Deleting Cookie {}", cookieName);
    MultiValueMap<String, HttpCookie> cookies = request.getCookies();

    // Verifica se il cookie esiste
    if (ObjectUtils.isEmpty(cookies) || ObjectUtils.isEmpty(cookies.get(cookieName))) {
      LOG.debug("Cookie {} not found", cookieName);
      return;
    }

    // Creazione di un cookie con lo stesso nome e un tempo di scadenza nel passato
    ResponseCookie cookieToDelete =
        ResponseCookie.from(cookieName, "").maxAge(Duration.ofSeconds(0)).path("/").build();

    // Aggiungi il cookie di cancellazione alla risposta
    response.addCookie(cookieToDelete);

    LOG.debug("Cookie {} deleted", cookieName);
  }
}
