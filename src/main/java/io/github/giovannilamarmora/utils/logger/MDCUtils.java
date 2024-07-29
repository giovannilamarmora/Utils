package io.github.giovannilamarmora.utils.logger;

import static io.github.giovannilamarmora.utils.context.ContextConfig.*;
import static io.github.giovannilamarmora.utils.context.ContextConfig.ENV;

import io.github.giovannilamarmora.utils.context.TraceUtils;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.micrometer.context.ContextRegistry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

@Component
public abstract class MDCUtils {

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static void setDataIntoMDC(String name, String data) {
    if (ObjectUtils.isEmpty(name) || ObjectUtils.isEmpty(data)) {
      return;
    }
    MDC.put(name, data);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static void registerMDC(String mdcKey) {
    Supplier<String> getMDC = () -> MDC.get(mdcKey);
    Consumer<String> putMDC = value -> MDC.put(mdcKey, value);
    Runnable removeMDC = () -> MDC.remove(mdcKey);
    ContextRegistry.getInstance().registerThreadLocalAccessor(mdcKey, getMDC, putMDC, removeMDC);
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static Mono<Void> registerDefaultMDC(String env) {
    return Mono.deferContextual(
            contextView -> {
              String spanID = "";
              String traceID = "";
              String parentID = "";

              try {
                spanID = TraceUtils.getTraceID();
                MDC.put(TRACE_ID.getValue(), spanID);
              } catch (Exception exception) {
                MDC.put(TRACE_ID.getValue(), exception.getMessage());
              }

              try {
                traceID = TraceUtils.getSpanID();
                MDC.put(SPAN_ID.getValue(), traceID);
              } catch (Exception exception) {
                MDC.put(SPAN_ID.getValue(), exception.getMessage());
              }

              try {
                parentID = TraceUtils.getParentID();
                MDC.put(PARENT_ID.getValue(), parentID);
              } catch (Exception exception) {
                parentID = exception.getMessage();
                MDC.put(PARENT_ID.getValue(), exception.getMessage());
              }

              if (!ObjectUtils.isEmpty(env)) {
                MDC.put(ENV.getValue(), env);
              }

              // Propaga il contesto MDC nel contesto reattivo
              return Mono.empty()
                  .contextWrite(
                      Context.of(
                          TRACE_ID.getValue(), traceID,
                          SPAN_ID.getValue(), spanID,
                          PARENT_ID.getValue(), parentID,
                          ENV.getValue(), env))
                  .doFinally(signalType -> MDC.clear());
            })
        .then();
  }
}
