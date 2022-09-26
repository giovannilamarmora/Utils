package com.github.giovannilamarmora.utils.interceptors;

import org.slf4j.Logger;

import java.time.Instant;

public class LogTimeTracker {
  static String DEVMODE = "DEV";
  private final ActionType actionType;
  private final String methodName;
  private final String userId;
  private final String correlationId;
  private final String sessionId;
  private final String logMode;
  private final long start;
  private final String action;

  private LogTimeTracker(
      ActionType actionType,
      String methodName,
      String userId,
      String correlationId,
      String sessionId,
      String logMode,
      String action) {
    super();
    this.actionType = actionType;
    this.methodName = methodName;
    this.userId = userId;
    this.correlationId = correlationId;
    this.sessionId = sessionId;
    this.logMode = logMode;
    this.action = action;
    this.start = Instant.now().toEpochMilli();
  }

  public static LogTimeTracker startInvocation(
      ActionType type,
      String methodName,
      String userId,
      String correlationId,
      String sessionId,
      String logMode,
      String action) {
    return new LogTimeTracker(type, methodName, userId, correlationId, sessionId, logMode, action);
  }

  public void trackFailure(Logger LOG, Exception e) {
    Throwable root_cause = getRootCause(e);
    String stackTrace = "";
    for (StackTraceElement sel : e.getStackTrace()) {
      stackTrace += sel.toString() + (DEVMODE.equalsIgnoreCase(logMode) ? "\n\t" : " >> ");
    }
    if (root_cause != null) {
      LOG.error(
          "action_type={}, method={}, user={}, action={}, correlation_id={}, session_id={}, time_taken={}, status=KO, exception={}, description={}, root_exception={}, root_description={}, stacktrace={}",
          this.actionType,
          this.methodName,
          this.userId,
          this.action,
          this.correlationId,
          this.sessionId,
          getDeltaInMilli(),
          getClassName(e),
          getMessage(e),
          getClassName(root_cause),
          root_cause.getMessage(),
          stackTrace);
      return;
    }
    LOG.error(
        "action_type={}, method={}, user={}, action={}, correlation_id={}, session_id={}, time_taken={}, status=KO, exception={}, description={}, stacktrace={}",
        this.actionType,
        this.methodName,
        this.userId,
        this.action,
        this.correlationId,
        this.sessionId,
        getDeltaInMilli(),
        getClassName(e),
        getMessage(e),
        stackTrace);
  }

  public void trackSuccess(Logger LOG) {
    LOG.info(
        "action_type={}, method={}, user={}, action={}, correlation_id={}, session_id={}, time_taken={}, status=OK",
        this.actionType,
        this.methodName,
        this.userId,
        this.action,
        this.correlationId,
        this.sessionId,
        getDeltaInMilli());
  }

  private long getDeltaInMilli() {
    return Instant.now().toEpochMilli() - this.start;
  }

  private String getMessage(Exception e) {
    if (e == null) {
      return null;
    }
    return e.getMessage();
  }

  private Throwable getRootCause(Throwable e) {
    if (e == null) {
      return null;
    }
    Throwable cause = e;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    return cause;
  }

  private String getClassName(Object o) {
    if (o == null) {
      return null;
    }
    Class c = o.getClass();
    if (c == null) {
      return "<null class>";
    }
    return c.getName();
  }

  public enum ActionType {
    APP_ENDPOINT,
    APP_LOGIC,
    APP_EXTERNAL
  }
}
