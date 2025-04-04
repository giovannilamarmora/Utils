package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;

public interface ResponseManager {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void setCookieAndHeaderData(
      String headerName, String headerValue, ServerHttpResponse response) {
    HeaderManager.addOrSetHeaderInResponse(headerName, headerValue, response);
    CookieManager.setCookieInResponse(headerName, headerValue, response);
  }

  /**
   * Sets both an HTTP header and a cookie in the response, ensuring the same value is stored in
   * both mechanisms. This is useful for authentication or session tracking, where data must be
   * accessible via headers and cookies across different client requests.
   *
   * <p>The method performs the following actions:
   *
   * <ul>
   *   <li>Adds or updates the specified header in the HTTP response.
   *   <li>Sets a corresponding HTTP cookie, dynamically determining the appropriate domain using
   *       the {@code Referer}, {@code Origin}, or {@code Host} header.
   * </ul>
   *
   * <h3>Domain Handling for Cookies:</h3>
   *
   * <ul>
   *   <li>If the request contains a {@code Referer}, the cookie domain is extracted from it.
   *   <li>If {@code Referer} is not available, the domain is taken from the {@code Origin} header.
   *   <li>If neither is available, the method falls back to using the {@code Host} header.
   *   <li>If no domain information is found, the cookie is set without a domain, making it valid
   *       only for the current authentication service.
   * </ul>
   *
   * <h3>Use Case Examples:</h3>
   *
   * <ul>
   *   <li>Ensuring session tokens are available in both headers and cookies for flexibility.
   *   <li>Allowing frontend applications to retrieve session-related data from either headers or
   *       cookies.
   *   <li>Providing a fallback mechanism in case one method (cookies or headers) is unavailable in
   *       certain environments.
   * </ul>
   *
   * @param headerName The name of the HTTP header to be set.
   * @param headerValue The value assigned to both the HTTP header and the cookie.
   * @param response The {@link ServerHttpResponse} object where the header and cookie will be set.
   * @param request The {@link ServerHttpRequest} object used to determine the appropriate cookie
   *     domain.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void setCookieAndHeaderData(
      String headerName,
      String headerValue,
      ServerHttpResponse response,
      ServerHttpRequest request) {

    // Set the specified header in the response
    HeaderManager.addOrSetHeaderInResponse(headerName, headerValue, response);

    // Set the corresponding cookie with domain handling based on Referer, Origin, or Host
    CookieManager.setCookieInResponse(headerName, headerValue, response, request);
  }

  /**
   * Sets both an HTTP header and a cookie in the response, ensuring the same value is stored in
   * both mechanisms. This is useful for authentication or session tracking, where data must be
   * accessible via headers and cookies across different client requests.
   *
   * <p>The method performs the following actions:
   *
   * <ul>
   *   <li>Adds or updates the specified header in the HTTP response.
   *   <li>Sets a corresponding HTTP cookie, dynamically determining the appropriate domain using
   *       the {@code Referer}, {@code Origin}, or {@code Host} header.
   * </ul>
   *
   * <h3>Domain Handling for Cookies:</h3>
   *
   * <ul>
   *   <li>If the request contains a {@code Referer}, the cookie domain is extracted from it.
   *   <li>If {@code Referer} is not available, the domain is taken from the {@code Origin} header.
   *   <li>If neither is available, the method falls back to using the {@code Host} header.
   *   <li>If no domain information is found, the cookie is set without a domain, making it valid
   *       only for the current authentication service.
   * </ul>
   *
   * <h3>Use Case Examples:</h3>
   *
   * <ul>
   *   <li>Ensuring session tokens are available in both headers and cookies for flexibility.
   *   <li>Allowing frontend applications to retrieve session-related data from either headers or
   *       cookies.
   *   <li>Providing a fallback mechanism in case one method (cookies or headers) is unavailable in
   *       certain environments.
   * </ul>
   *
   * @param headerName The name of the HTTP header to be set.
   * @param headerValue The value assigned to both the HTTP header and the cookie.
   * @param domain The domain used to determine the appropriate cookie.
   * @param response The {@link ServerHttpResponse} object where the header and cookie will be set.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void setCookieAndHeaderData(
      String headerName, String headerValue, String domain, ServerHttpResponse response) {

    // Set the specified header in the response
    HeaderManager.addOrSetHeaderInResponse(headerName, headerValue, response);

    // Set the corresponding cookie with domain handling based on Referer, Origin, or Host
    CookieManager.setCookieInResponse(headerName, headerValue, domain, response);
  }
}
