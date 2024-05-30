package io.github.giovannilamarmora.utils.interceptors;

import io.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Logged
@Component
public class LoggerInterceptor implements Serializable {
  private static final long serialVersionUID = 5001545131635232118L;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Pointcut("@annotation(io.github.giovannilamarmora.utils.interceptors.LogInterceptor)")
  public void annotationPointcut() {}

  @Around("annotationPointcut()")
  public Object processMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
    Method method = signature.getMethod();
    LogInterceptor annotation = method.getAnnotation(LogInterceptor.class);

    // avoid to track all invocation because they are not so usefull
    if (annotation == null) {
      return proceedingJoinPoint.proceed();
    } else if (annotation.type() == LogTimeTracker.ActionType.CONTROLLER) {
      CorrelationIdUtils.generateCorrelationId();
    }
    String className = method.getDeclaringClass().getSimpleName();
    String methodName = className + "." + method.getName();
    LogTimeTracker tracker =
        LogTimeTracker.startInvocation(
            annotation.type(), methodName, CorrelationIdUtils.getCorrelationId());
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
