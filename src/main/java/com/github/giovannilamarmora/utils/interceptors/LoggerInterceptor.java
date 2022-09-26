package com.github.giovannilamarmora.utils.interceptors;

import com.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;

@Aspect
@Logged
@Component
public class LoggerInterceptor implements Serializable {
  private static final long serialVersionUID = 5001545131635232118L;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Value("${log-stack-mode}")
  private String logMode;

  @Around("@annotation(LogInterceptor)")
  public Object processMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = signature.getMethod();
    LogInterceptor annotation = method.getAnnotation(LogInterceptor.class);

    // avoid to track all invocation because they are not so usefull
    if (annotation == null) {
      return proceedingJoinPoint.proceed();
    }
    String className = method.getDeclaringClass().getName();
    String methodName = className + "." + method.getName();
    LogTimeTracker tracker =
        LogTimeTracker.startInvocation(
            annotation.type(),
            methodName,
            "JWTUtil.getUserId()",
            CorrelationIdUtils.getCorrelationId(),
            "SessionIDUtils.getSessionId()",
            logMode,
            null != "operation" ? "operation.description()" : "");
    Object obj;
    try {
      obj = proceedingJoinPoint.proceed();
    } catch (Exception e) {
      tracker.trackFailure(LOG, e);
      throw e;
    }
    tracker.trackSuccess(LOG);
    return obj;
  }
}
