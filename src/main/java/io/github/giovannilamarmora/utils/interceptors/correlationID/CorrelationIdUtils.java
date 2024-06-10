package io.github.giovannilamarmora.utils.interceptors.correlationID;

import java.util.UUID;

public interface CorrelationIdUtils {
  String CORRELATION_HEADER_NAME = "X-Correlation-ID";
  String CORRELATION_MDC_NAME = "correlationId";

  ThreadLocal<String> correlationIdTL =
      ThreadLocal.withInitial(CorrelationIdUtils::generateCorrelationId);

  static String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }

  static String getCorrelationId() {
    return correlationIdTL.get();
  }

  static void setCorrelationId(String correlationId) {
    correlationIdTL.set(correlationId);
  }
}
