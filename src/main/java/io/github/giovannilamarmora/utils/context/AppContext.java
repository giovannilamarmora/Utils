package io.github.giovannilamarmora.utils.context;

import java.util.Optional;
import org.slf4j.MDC;

public interface AppContext {

  static String getApplicationName() {
    return Optional.ofNullable(MDC.get(ContextConfig.APP_NAME.getValue()))
        .orElseThrow(() -> new TracingException("No " + ContextConfig.APP_NAME.getValue()));
  }

  static String getApplicationVersion() {
    return Optional.ofNullable(MDC.get(ContextConfig.APP_VERSION.getValue()))
        .orElseThrow(() -> new TracingException("No " + ContextConfig.APP_VERSION.getValue()));
  }
}
