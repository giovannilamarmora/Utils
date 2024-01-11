package io.github.giovannilamarmora.utils.config;

import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
public class UtilsPropertiesManager {

  private static final Logger LOG = LoggerFactory.getLogger(UtilsPropertiesManager.class);

  /** WeBClient Prop */
  @Value("#{new Integer(${rest.webClient.timeout.read:15000})}")
  private Integer readTimeout;

  @Value("#{new Integer(${rest.webClient.timeout.write:15000})}")
  private Integer writeTimeout;

  @Value("#{new Integer(${rest.webClient.timeout.connection:10000})}")
  private Integer connectionTimeout;

  /** Cors Prop */
  @Value("#{new Boolean(${app.cors.enabled:false})}")
  private Boolean isCorsEnabled;

  /** UtilsException */
  @Value("#{new Boolean(${app.exception.stacktrace.utilsException.active:true})}")
  private Boolean isUtilsStackTraceActive;

  @Value("#{new Boolean(${app.exception.stacktrace.utilsException.debug:true})}")
  private Boolean isDebugUtilsStackTraceActive;

  /** LogTimeTracker */
  @Value("#{new Boolean(${app.interceptors.actionType.success.debug:true})}")
  private Boolean isLevelDebugActive;

  @PostConstruct
  private void init() {
    LOG.info("readTimeout: {}", readTimeout);
    LOG.info("writeTimeout: {}", writeTimeout);
    LOG.info("connectionTimeout: {}", connectionTimeout);
    LOG.info("isCorsEnabled: {}", isCorsEnabled);
    LOG.info("isUtilsStackTraceActive: {}", isUtilsStackTraceActive);
    LOG.info("isDebugUtilsStackTraceActive: {}", isDebugUtilsStackTraceActive);
    LOG.info("isLevelDebugActive: {}", isLevelDebugActive);
  }
}
