package io.github.giovannilamarmora.utils;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;

public class HealthCheck {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String startHealthCheck() {
    return "STATUS_OK";
  }
}
