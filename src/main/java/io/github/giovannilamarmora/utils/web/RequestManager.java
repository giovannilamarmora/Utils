package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;
import org.springframework.http.server.reactive.ServerHttpRequest;

public interface RequestManager {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getCookieOrHeaderData(String name, ServerHttpRequest request) {
    String cookie = CookieManager.getCookie(name, request);
    String header = HeaderManager.getHeader(name, request);
    if (ObjectToolkit.isNullOrEmpty(header)) return cookie;
    return header;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getCookieOrHeaderData(
      String cookieName, String headerName, ServerHttpRequest request) {
    String cookie = CookieManager.getCookie(cookieName, request);
    String header = HeaderManager.getHeader(headerName, request);
    if (ObjectToolkit.isNullOrEmpty(header)) return cookie;
    return header;
  }
}
