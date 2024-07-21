package io.github.giovannilamarmora.utils.context;

import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;

public interface TraceUtils {

  ThreadLocal<String> spanIdTL = ThreadLocal.withInitial(TraceUtils::generateTrace);
  ThreadLocal<String> traceIdTL = ThreadLocal.withInitial(TraceUtils::generateTrace);

  static String generateTrace() {
    return UUID.randomUUID().toString();
  }

  static String getSpanID() {
    return Optional.ofNullable(MDC.get(ContextConfig.SPAN_ID.getValue()))
        .orElse(
            Optional.ofNullable(spanIdTL.get())
                .orElseThrow(
                    () ->
                        new TracingException(
                            ContextConfig.SPAN_ID.getValue() + " has not been initialized")));
  }

  static String getTraceID() {
    return Optional.ofNullable(MDC.get(ContextConfig.TRACE_ID.getValue()))
        .orElse(
            Optional.ofNullable(traceIdTL.get())
                .orElseThrow(
                    () ->
                        new TracingException(
                            ContextConfig.TRACE_ID.getValue() + " has not been initialized")));
  }

  static String getParentID() {
    return Optional.ofNullable(MDC.get(ContextConfig.PARENT_ID.getValue()))
        .orElseThrow(
            () ->
                new TracingException(
                    ContextConfig.PARENT_ID.getValue() + " has not been initialized"));
  }

  static String getEnvironment() {
    return Optional.ofNullable(MDC.get(ContextConfig.ENV.getValue()))
        .orElseThrow(() -> new TracingException("No " + ContextConfig.ENV.getValue()));
  }
}
