package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.springframework.http.server.reactive.ServerHttpResponse;

public interface ResponseManager {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static void setCookieAndHeaderData(
      String headerName, String headerValue, ServerHttpResponse response) {
    HeaderManager.addOrSetHeaderInResponse(headerName, headerValue, response);
    CookieManager.setCookieInResponse(headerName, headerValue, response);
  }
}
