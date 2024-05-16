package io.github.giovannilamarmora.utils.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Payload;
import com.google.cloud.logging.Severity;
import com.google.cloud.logging.logback.LoggingEventEnhancer;
import io.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class GoogleLogConfig implements LoggingEventEnhancer {

  @Override
  public void enhanceLogEntry(LogEntry.Builder logEntry, ILoggingEvent e) {
    // Load properties from YAML file
    InputStream inputStream =
        GoogleLogConfig.class.getClassLoader().getResourceAsStream("application.yml");
    Yaml yaml = new Yaml();
    Map<String, JsonNode> yamlProperties = yaml.load(inputStream);

    // Get value of "env" property
    String env = String.valueOf(yamlProperties.get("env"));

    // Set log severity
    logEntry.setSeverity(mapSeverity(e.getLevel()));

    // Build log payload
    HashMap<String, Object> map = new HashMap<>();
    map.put("env", env);
    map.put("thread", e.getThreadName());
    map.put("context", e.getLoggerContextVO().getName());
    map.put("logger", e.getLoggerName());
    map.put("mdc", e.getMDCPropertyMap());

    // Add existing payload data
    Payload.JsonPayload payload = logEntry.build().getPayload();
    map.putAll(payload.getDataAsMap());

    logEntry.setPayload(Payload.JsonPayload.of(map));

    // Set log trace
    logEntry.setTrace(CorrelationIdUtils.getCorrelationId());
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
