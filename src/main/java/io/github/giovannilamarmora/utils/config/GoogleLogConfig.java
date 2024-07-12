package io.github.giovannilamarmora.utils.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.cloud.logging.logback.LoggingEventEnhancer;
import io.github.giovannilamarmora.utils.context.ContextConfig;
import io.github.giovannilamarmora.utils.context.TraceUtils;
import java.util.HashMap;

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
    map.put("mdc", e.getMDCPropertyMap());

    // Add existing payload data
    Payload.JsonPayload payload = logEntry.build().getPayload();
    map.putAll(payload.getDataAsMap());

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
    switch (level.toInt()) {
      case Level.ERROR_INT:
        return Severity.ERROR;
      case Level.WARN_INT:
        return Severity.WARNING;
      case Level.INFO_INT:
        return Severity.INFO;
      case Level.DEBUG_INT, Level.TRACE_INT:
        return Severity.DEBUG;
      default:
        return Severity.DEFAULT;
    }
  }
}
