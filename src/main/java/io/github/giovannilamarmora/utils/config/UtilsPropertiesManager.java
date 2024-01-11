package io.github.giovannilamarmora.utils.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UtilsPropertiesManager {

  private static final Logger LOG = LoggerFactory.getLogger(UtilsPropertiesManager.class);
  private final Properties properties = new Properties();

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

  public UtilsPropertiesManager() {
    loadProperties();
  }

  private void loadProperties() {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.yml"); ) {
      properties.load(input);
    } catch (IOException e) {
      LOG.error("File not found");
      e.printStackTrace();
    }
  }

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

  public Boolean getIsLevelDebugActive() {
    return isLevelDebugActive != null
        ? isLevelDebugActive
        : Boolean.parseBoolean(
            properties.getProperty("app.interceptors.actionType.success.debug", "true"));
  }
}
