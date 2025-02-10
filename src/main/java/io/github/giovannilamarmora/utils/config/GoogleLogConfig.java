package io.github.giovannilamarmora.utils.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.cloud.logging.logback.LoggingEventEnhancer;
import io.github.giovannilamarmora.utils.context.AppContext;
import io.github.giovannilamarmora.utils.context.ContextConfig;
import io.github.giovannilamarmora.utils.context.TraceUtils;
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;
import java.util.HashMap;
import java.util.Map;

public class GoogleLogConfig implements LoggingEventEnhancer {

  @Override
  public void enhanceLogEntry(LogEntry.Builder logEntry, ILoggingEvent e) {

    // Set log severity
    logEntry.setSeverity(mapSeverity(e.getLevel()));

    // Build log payload
    HashMap<String, Object> map = new HashMap<>();
    map.put("thread", e.getThreadName());
    map.put("context", e.getLoggerContextVO().getName());
    map.put("logger", e.getLoggerName());
    Map<String, String> mdcValues = new HashMap<>(e.getMDCPropertyMap());
    mdcValues.remove(ContextConfig.ENV.getValue());
    mdcValues.remove(ContextConfig.APP_NAME.getValue());
    mdcValues.remove(ContextConfig.APP_VERSION.getValue());
    map.put("mdc", mdcValues);

    // Add existing payload data
    Payload.JsonPayload payload = logEntry.build().getPayload();
    map.putAll(payload.getDataAsMap());

    // Check if there's a ThrowableProxy (exception)
    IThrowableProxy throwableProxy = e.getThrowableProxy();
    if (!ObjectToolkit.isNullOrEmpty(throwableProxy)) {
      // Include exception message and stack trace in the log payload
      map.put("exception_message", throwableProxy.getMessage());

      // Get the first element in the stack trace (location of the error)
      StackTraceElementProxy[] stackTraceElements = throwableProxy.getStackTraceElementProxyArray();
      if (stackTraceElements != null && stackTraceElements.length > 0) {
        StackTraceElement firstElement = stackTraceElements[0].getStackTraceElement();
        map.put("error_class", firstElement.getClassName());
        map.put("error_method", firstElement.getMethodName());
        map.put(
            "error_line",
            firstElement.getLineNumber()); // Add the line number where the exception occurred
      }
    }

    try {
      map.put(ContextConfig.ENV.getValue(), TraceUtils.getEnvironment());
    } catch (Exception exception) {
      // map.put(ContextConfig.ENV.getValue(), exception.getMessage());
    }

    try {
      map.put(ContextConfig.APP_NAME.getValue(), AppContext.getApplicationName());
    } catch (Exception exception) {
      // map.put(ContextConfig.APP_NAME.getValue(), exception.getMessage());
    }

    try {
      map.put(ContextConfig.APP_VERSION.getValue(), AppContext.getApplicationVersion());
    } catch (Exception exception) {
      // map.put(ContextConfig.APP_VERSION.getValue(), exception.getMessage());
    }

    logEntry.setPayload(Payload.JsonPayload.of(map));

    // Set log trace
    try {
      logEntry.setTrace(TraceUtils.getTraceID());
    } catch (Exception exception) {
      logEntry.addLabel(ContextConfig.TRACE_ID.getValue(), exception.getMessage());
    }

    try {
      logEntry.setSpanId(TraceUtils.getSpanID());
    } catch (Exception exception) {
      logEntry.addLabel(ContextConfig.SPAN_ID.getValue(), exception.getMessage());
    }

    try {
      logEntry.addLabel(ContextConfig.ENV.getValue(), TraceUtils.getEnvironment());
    } catch (Exception exception) {
      logEntry.addLabel(ContextConfig.ENV.getValue(), exception.getMessage());
    }
  }

  // Utility method to map Logback log level to Google Cloud Logging severity
  private Severity mapSeverity(Level level) {
    return switch (level.toInt()) {
      case Level.ERROR_INT -> Severity.ERROR;
      case Level.WARN_INT -> Severity.WARNING;
      case Level.INFO_INT -> Severity.INFO;
      case Level.DEBUG_INT, Level.TRACE_INT -> Severity.DEBUG;
      default -> Severity.DEFAULT;
    };
  }
}
