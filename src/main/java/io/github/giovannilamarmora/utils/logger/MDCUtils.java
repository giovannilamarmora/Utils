package io.github.giovannilamarmora.utils.logger;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public abstract class MDCUtils {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static void setDataIntoMDC(String name, String data) {
    if (ObjectUtils.isEmpty(name) || ObjectUtils.isEmpty(data)) {
      return;
    }
    MDC.put(name, data);
  }
}
