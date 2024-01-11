package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.interceptors.Logged;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@Logged
@NoArgsConstructor
public class UtilsPropertiesManager {

  /** WeBClient Prop */
  @Value(value = "#{new Integer(${rest.webClient.timeout.read:15000})}")
  private Integer readTimeout;

  @Value(value = "#{new Integer(${rest.webClient.timeout.write:15000})}")
  private Integer writeTimeout;

  @Value(value = "#{new Integer(${rest.webClient.timeout.connection:10000})}")
  private Integer connectionTimeout;

  /** Cors Prop */
  @Value(value = "#{new Boolean(${app.cors.enabled:false})}")
  private Boolean isCorsEnabled;

  /** UtilsException */
  @Value(value = "#{new Boolean(${app.exception.stacktrace.utilsException.active:true})}")
  private Boolean isUtilsStackTraceActive;

  @Value(value = "#{new Boolean(${app.exception.stacktrace.utilsException.debug:true})}")
  private Boolean isDebugUtilsStackTraceActive;

  /** LogTimeTracker */
  @Value(value = "#{new Boolean(${app.interceptors.actionType.success.debug:true})}")
  private Boolean isLevelDebugActive;
}
