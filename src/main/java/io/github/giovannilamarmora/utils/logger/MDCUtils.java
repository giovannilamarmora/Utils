package io.github.giovannilamarmora.utils.logger;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.micrometer.context.ContextRegistry;
import java.util.function.Consumer;
import java.util.function.Supplier;
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

  public static void registerMDC(String mdcKey) {
    Supplier<String> getMDC = () -> MDC.get(mdcKey);
    Consumer<String> putMDC = value -> MDC.put(mdcKey, value);
    Runnable removeMDC = () -> MDC.remove(mdcKey);
    ContextRegistry.getInstance().registerThreadLocalAccessor(mdcKey, getMDC, putMDC, removeMDC);
  }
}
