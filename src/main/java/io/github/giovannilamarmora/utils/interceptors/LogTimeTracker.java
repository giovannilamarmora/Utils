package io.github.giovannilamarmora.utils.interceptors;

import java.time.Instant;
import org.slf4j.Logger;

public class LogTimeTracker {
  private final ActionType actionType;
  private final String methodName;
  private final String correlationId;
  private final long start;

  private LogTimeTracker(ActionType actionType, String methodName, String correlationId) {
    super();
    this.actionType = actionType;
    this.methodName = methodName;
    this.correlationId = correlationId;
    this.start = Instant.now().toEpochMilli();
  }

  public static LogTimeTracker startInvocation(
      ActionType type, String methodName, String correlationId) {
    return new LogTimeTracker(type, methodName, correlationId);
  }

  public void trackFailure(Logger LOG, Exception e) {
    Throwable root_cause = getRootCause(e);
    /*String stackTrace = "";
    for (StackTraceElement sel : e.getStackTrace()) {
      stackTrace += sel.toString() + (DEVMODE.equalsIgnoreCase(logMode) ? "\n\t" : " >> ");
    }*/
    if (root_cause != null) {
      LOG.error(
          "[ACTION_TYPE]={}, [METHOD]={}, [CORRELATION_ID]={}, [TIME_TAKEN]={}, [STATUS]=KO, [EXCEPTION]={}, [DESCRIPTION]={}, [ROOT_EXCEPTION]={}, [ROOT_DESCRIPTION]={}",
          this.actionType,
          this.methodName,
          this.correlationId,
          getDeltaInMilli(),
          getClassName(e),
          getMessage(e),
          getClassName(root_cause),
          root_cause.getMessage());
      return;
    }
    LOG.error(
        "[ACTION_TYPE]={}, [METHOD]={}, [CORRELATION_ID]={}, [TIME_TAKEN]={}, [STATUS]=KO, [EXCEPTION]={}, [DESCRIPTION]={}",
        this.actionType,
        this.methodName,
        this.correlationId,
        getDeltaInMilli(),
        getClassName(e),
        getMessage(e));
  }

  public void trackSuccess(Logger LOG) {
    if (this.actionType.equals(ActionType.CONTROLLER))
      LOG.info(
          "[ACTION_TYPE]={}, [METHOD]={}, [CORRELATION_ID]={}, [TIME_TAKEN]={}, [STATUS]=OK",
          this.actionType,
          this.methodName,
          this.correlationId,
          getDeltaInMilli());
  }

  private long getDeltaInMilli() {
    return Instant.now().toEpochMilli() - this.start;
  }

  private String getMessage(Exception e) {
    if (e == null) return null;
    return e.getMessage();
  }

  private Throwable getRootCause(Throwable e) {
    if (e == null) return null;
    Throwable cause = e;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    return cause;
  }

  private String getClassName(Object o) {
    if (o == null) return null;

    Class c = o.getClass();
    if (c == null) {
      return "<null class>";
    }
    return c.getName();
  }

  public enum ActionType {
    CONTROLLER,
    SERVICE,
    MAPPER,
    EXTERNAL,
    CACHE,
    SCHEDULER,
    INTERCEPTOR,
    GRPC,
    VALIDATOR,
    UTILS_LOGGER
  }
}
