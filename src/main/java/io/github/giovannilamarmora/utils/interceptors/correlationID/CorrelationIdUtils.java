package io.github.giovannilamarmora.utils.interceptors.correlationID;

import java.util.UUID;

public class CorrelationIdUtils {
  public static final String CORRELATION_HEADER_NAME = "X-CorrelationID";
  public static final String CORRELATION_MDC_NAME = "correlationId";
  private static final ThreadLocal<String> correlationIdTL =
      ThreadLocal.withInitial(CorrelationIdUtils::generateCorrelationId);

  public static String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }

  public static String getCorrelationId() {
    return correlationIdTL.get();
  }

  public static void setCorrelationId(String correlationId) {
    correlationIdTL.set(correlationId);
  }
}
