package io.github.giovannilamarmora.utils.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoggerFilter implements Logger {

  private final Logger logger;
  private final SensitiveDataMasker masker;
  private final ObjectSanitizer sanitizer;

  @Value("${sensitiveData}")
  private String sensitiveData =
      // "jweSecret|Authorization|access_token|id_token|refresh_token|x-api-key|clientSecret";

      "password|token|Authorization|Bearer|Basic|access[_-]?token|refresh[_-]?token|strapi[_-]?token|jwt|jwtSecret|jweSecret|registrationToken|secretKey|tokenReset";

  private LoggerFilter() {
    this.logger = LoggerFactory.getLogger(this.getClass());
    this.masker = new SensitiveDataMasker(sensitiveData);
    this.sanitizer = new ObjectSanitizer(sensitiveData);
  }

  private LoggerFilter(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
    this.masker = new SensitiveDataMasker(sensitiveData);
    this.sanitizer = new ObjectSanitizer(sensitiveData);
  }

  private LoggerFilter(Class<?> clazz, String sensitiveRegex) {
    this.logger = LoggerFactory.getLogger(clazz);
    this.masker = new SensitiveDataMasker(sensitiveRegex);
    this.sanitizer = new ObjectSanitizer(sensitiveRegex);
  }

  public static Logger getLogger(Class<?> clazz) {
    return new LoggerFilter(clazz);
  }

  public static Logger getLogger(Class<?> clazz, String sensitive) {
    return new LoggerFilter(clazz, sensitive);
  }

  private String mask(String msg) {
    return masker.mask(msg);
  }

  private Object[] filter(Object... args) {
    return sanitizer.sanitize(args);
  }

  // === METODI DI LOG (info/debug/trace/warn/error) ===

  // INFO
  @Override
  public void info(String msg) {
    logger.info(mask(msg));
  }

  @Override
  public void info(String msg, Object o) {
    logger.info(mask(msg), filter(o));
  }

  @Override
  public void info(String msg, Object o1, Object o2) {
    logger.info(mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void info(String msg, Object... args) {
    logger.info(mask(msg), filter(args));
  }

  @Override
  public void info(String msg, Throwable t) {
    logger.info(mask(msg), t);
  }

  @Override
  public void info(Marker m, String msg) {
    logger.info(m, mask(msg));
  }

  @Override
  public void info(Marker m, String msg, Object o) {
    logger.info(m, mask(msg), filter(o));
  }

  @Override
  public void info(Marker m, String msg, Object o1, Object o2) {
    logger.info(m, mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void info(Marker m, String msg, Object... args) {
    logger.info(m, mask(msg), filter(args));
  }

  @Override
  public void info(Marker m, String msg, Throwable t) {
    logger.info(m, mask(msg), t);
  }

  // DEBUG
  @Override
  public void debug(String msg) {
    logger.debug(mask(msg));
  }

  @Override
  public void debug(String msg, Object o) {
    logger.debug(mask(msg), filter(o));
  }

  @Override
  public void debug(String msg, Object o1, Object o2) {
    logger.debug(mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void debug(String msg, Object... args) {
    logger.debug(mask(msg), filter(args));
  }

  @Override
  public void debug(String msg, Throwable t) {
    logger.debug(mask(msg), t);
  }

  @Override
  public void debug(Marker m, String msg) {
    logger.debug(m, mask(msg));
  }

  @Override
  public void debug(Marker m, String msg, Object o) {
    logger.debug(m, mask(msg), filter(o));
  }

  @Override
  public void debug(Marker m, String msg, Object o1, Object o2) {
    logger.debug(m, mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void debug(Marker m, String msg, Object... args) {
    logger.debug(m, mask(msg), filter(args));
  }

  @Override
  public void debug(Marker m, String msg, Throwable t) {
    logger.debug(m, mask(msg), t);
  }

  // TRACE
  @Override
  public void trace(String msg) {
    logger.trace(mask(msg));
  }

  @Override
  public void trace(String msg, Object o) {
    logger.trace(mask(msg), filter(o));
  }

  @Override
  public void trace(String msg, Object o1, Object o2) {
    logger.trace(mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void trace(String msg, Object... args) {
    logger.trace(mask(msg), filter(args));
  }

  @Override
  public void trace(String msg, Throwable t) {
    logger.trace(mask(msg), t);
  }

  @Override
  public void trace(Marker m, String msg) {
    logger.trace(m, mask(msg));
  }

  @Override
  public void trace(Marker m, String msg, Object o) {
    logger.trace(m, mask(msg), filter(o));
  }

  @Override
  public void trace(Marker m, String msg, Object o1, Object o2) {
    logger.trace(m, mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void trace(Marker m, String msg, Object... args) {
    logger.trace(m, mask(msg), filter(args));
  }

  @Override
  public void trace(Marker m, String msg, Throwable t) {
    logger.trace(m, mask(msg), t);
  }

  // WARN
  @Override
  public void warn(String msg) {
    logger.warn(mask(msg));
  }

  @Override
  public void warn(String msg, Object o) {
    logger.warn(mask(msg), filter(o));
  }

  @Override
  public void warn(String msg, Object o1, Object o2) {
    logger.warn(mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void warn(String msg, Object... args) {
    logger.warn(mask(msg), filter(args));
  }

  @Override
  public void warn(String msg, Throwable t) {
    logger.warn(mask(msg), t);
  }

  @Override
  public void warn(Marker m, String msg) {
    logger.warn(m, mask(msg));
  }

  @Override
  public void warn(Marker m, String msg, Object o) {
    logger.warn(m, mask(msg), filter(o));
  }

  @Override
  public void warn(Marker m, String msg, Object o1, Object o2) {
    logger.warn(m, mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void warn(Marker m, String msg, Object... args) {
    logger.warn(m, mask(msg), filter(args));
  }

  @Override
  public void warn(Marker m, String msg, Throwable t) {
    logger.warn(m, mask(msg), t);
  }

  // ERROR
  @Override
  public void error(String msg) {
    logger.error(mask(msg));
  }

  @Override
  public void error(String msg, Object o) {
    logger.error(mask(msg), filter(o));
  }

  @Override
  public void error(String msg, Object o1, Object o2) {
    logger.error(mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void error(String msg, Object... args) {
    logger.error(mask(msg), filter(args));
  }

  @Override
  public void error(String msg, Throwable t) {
    logger.error(mask(msg), t);
  }

  @Override
  public void error(Marker m, String msg) {
    logger.error(m, mask(msg));
  }

  @Override
  public void error(Marker m, String msg, Object o) {
    logger.error(m, mask(msg), filter(o));
  }

  @Override
  public void error(Marker m, String msg, Object o1, Object o2) {
    logger.error(m, mask(msg), filter(o1), filter(o2));
  }

  @Override
  public void error(Marker m, String msg, Object... args) {
    logger.error(m, mask(msg), filter(args));
  }

  @Override
  public void error(Marker m, String msg, Throwable t) {
    logger.error(m, mask(msg), t);
  }

  // === STATUS ===
  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled(marker);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled(marker);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled(marker);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled(marker);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled(marker);
  }

  @Override
  public String getName() {
    return logger.getName();
  }
}
