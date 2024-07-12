package io.github.giovannilamarmora.utils.context;

import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.MDCUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;

@Component
public class ContextRegistry {

  private static final Logger logger = LoggerFilter.getLogger(ContextRegistry.class);

  @PostConstruct
  public void init() {
    logger.info("Initialize Base Propagation Context Registry");
    Hooks.enableAutomaticContextPropagation();
    MDCUtils.registerMDC(ContextConfig.TRACE_ID.getValue());
    MDCUtils.registerMDC(ContextConfig.SPAN_ID.getValue());
    MDCUtils.registerMDC(ContextConfig.PARENT_ID.getValue());
    MDCUtils.registerMDC(ContextConfig.ENV.getValue());
  }
}
