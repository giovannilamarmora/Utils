package io.github.giovannilamarmora.utils;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import java.util.HashMap;
import org.springframework.http.HttpStatus;

public class HealthCheck {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String startHealthCheck() {
    return new HashMap<String, String>().put("Health-Check-Status", "ACTIVE " + HttpStatus.OK);
  }
}
