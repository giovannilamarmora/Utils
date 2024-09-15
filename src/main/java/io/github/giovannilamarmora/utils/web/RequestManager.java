package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ObjectUtils;

public interface RequestManager {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getCookieOrHeaderData(String name, ServerHttpRequest request) {
    String cookie = CookieManager.getCookie(name, request);
    if (ObjectUtils.isEmpty(cookie)) return HeaderManager.getHeader(name, request);
    return cookie;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String getCookieOrHeaderData(
      String cookieName, String headerName, ServerHttpRequest request) {
    String cookie = CookieManager.getCookie(cookieName, request);
    if (ObjectUtils.isEmpty(cookie)) return HeaderManager.getHeader(headerName, request);
    return cookie;
  }
}
