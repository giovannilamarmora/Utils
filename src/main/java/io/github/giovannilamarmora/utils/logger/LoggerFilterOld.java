package io.github.giovannilamarmora.utils.logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class LoggerFilterOld implements Logger {

  private String SENSITIVE_DATA =
      "password|bearer|basic|token|Bearer|Basic|Authorization|access_token|accessToken|refresh_token|refreshToken|jwt";

  @Value("${sensitiveData}")
  private String sensitiveData;

  private Logger logger;

  // @PostConstruct
  // public void init() {
  //  // Imposta la chiave statica dopo che Spring ha iniettato la chiave
  //  staticAesKey = aesKey;
  // }

  public LoggerFilterOld() {}

  private LoggerFilterOld(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  public void setSensitiveData(String sensitiveData) {
    SENSITIVE_DATA = sensitiveData;
  }

  public static Logger getLogger(Class<?> clazz) {
    return new LoggerFilterOld(clazz);
  }

  private String maskSensitiveFields(String message) {
    String response = message;
    // Maschera i campi nel corpo del messaggio
    response =
        response.replaceAll("(?i)\"(" + SENSITIVE_DATA + ")\":\"[^\"]*\"", "\"$1\" : \"********\"");
    response =
        response.replaceAll(
            "(?i)\"(" + SENSITIVE_DATA + ")\"\\s*:\\s*\"[^\"]*\"", "\"$1\" : \"********\"");
    response =
        response.replaceAll(
            "(?i)\"(" + this.SENSITIVE_DATA + ")\"\\s*:\\s*\"([^\"]*)\"\\s*(,|$)",
            "\"$1\" : \"********\"$3");

    // Maschera i campi negli header con formato "NomeHeader: ValoreHeader"
    response = response.replaceAll("(?i)(" + SENSITIVE_DATA + "):\\s*\\S*", "$1: ********");
    // Maschera i campi negli header con formato "NomeHeader: ValoreHeader"
    response = response.replaceAll("(?i)(" + SENSITIVE_DATA + "):\\s*.*", "$1: ********");
    // Maschera i campi negli header con formato "NomeHeader: ValoreHeader"
    response = response.replaceAll("(?i)(" + SENSITIVE_DATA + "):\\s*\\S+", "$1: ********");
    return response;
  }

  private Object[] filterSensitiveFields(Object... objects) {
    List<Object> clonedObjects = new ArrayList<>();
    for (Object object : objects) {
      if (object != null && !isJavaBaseClass(object.getClass())) {
        clonedObjects.add(cloneAndFilterFields(object));
      } else {
        if (object instanceof String) {
          clonedObjects.add(maskSensitiveFields((String) object));
        } else clonedObjects.add(object); // Non filtrare oggetti del modulo java.base
      }
    }
    return clonedObjects.toArray();
  }

  private boolean isJavaBaseClass(Class<?> clazz) {
    return !ObjectUtils.isEmpty(clazz.getModule().getName())
        && clazz.getModule().getName().equals("java.base");
  }

  private Object cloneAndFilterFields(Object originalObject) {
    if (originalObject instanceof String) return maskSensitiveFields((String) originalObject);
    try {
      // Effettua la clonazione profonda dell'oggetto
      Class<?> clazz = originalObject.getClass();
      Constructor<?> constructor = clazz.getDeclaredConstructor();
      constructor.setAccessible(true);
      Object clonedObject = constructor.newInstance();
      for (Field field : originalObject.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        Object fieldValue = field.get(originalObject);
        if (fieldValue instanceof String) {
          String fieldName = field.getName();
          if (isSensitiveField(fieldName)) {
            fieldValue = "********"; // Maschera il valore del campo sensibile
          }
        }
        field.set(clonedObject, fieldValue);
      }
      return clonedObject;
    } catch (Exception e) {
      return originalObject;
    }
  }

  private boolean isSensitiveField(String fieldName) {
    // Verifica se il nome del campo è sensibile
    return fieldName.matches("(?i)(" + SENSITIVE_DATA + ")");
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public void info(String s) {
    logger.info(maskSensitiveFields(s));
  }

  @Override
  public void info(String s, Object o) {
    logger.info(maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void info(String s, Object o, Object o1) {
    logger.info(maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void info(String s, Object... objects) {
    logger.info(maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void info(String s, Throwable throwable) {
    logger.info(maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled(marker);
  }

  @Override
  public void info(Marker marker, String s) {
    logger.info(marker, maskSensitiveFields(s));
  }

  @Override
  public void info(Marker marker, String s, Object o) {
    logger.info(marker, maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void info(Marker marker, String s, Object o, Object o1) {
    logger.info(
        marker, maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void info(Marker marker, String s, Object... objects) {
    logger.info(marker, maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void info(Marker marker, String s, Throwable throwable) {
    logger.info(marker, maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void trace(String s) {
    logger.trace(maskSensitiveFields(s));
  }

  @Override
  public void trace(String s, Object o) {
    logger.trace(maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void trace(String s, Object o, Object o1) {
    logger.trace(maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void trace(String s, Object... objects) {
    logger.trace(maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void trace(String s, Throwable throwable) {
    logger.trace(maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled(marker);
  }

  @Override
  public void trace(Marker marker, String s) {
    logger.trace(marker, maskSensitiveFields(s));
  }

  @Override
  public void trace(Marker marker, String s, Object o) {
    logger.trace(marker, maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void trace(Marker marker, String s, Object o, Object o1) {
    logger.trace(
        marker, maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void trace(Marker marker, String s, Object... objects) {
    logger.trace(marker, maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void trace(Marker marker, String s, Throwable throwable) {
    logger.trace(marker, maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public void debug(String s) {
    logger.debug(maskSensitiveFields(s));
  }

  @Override
  public void debug(String s, Object o) {
    logger.debug(maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void debug(String s, Object o, Object o1) {
    logger.debug(maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void debug(String s, Object... objects) {
    logger.debug(maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void debug(String s, Throwable throwable) {
    logger.debug(maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled(marker);
  }

  @Override
  public void debug(Marker marker, String s) {
    logger.debug(marker, maskSensitiveFields(s));
  }

  @Override
  public void debug(Marker marker, String s, Object o) {
    logger.debug(marker, maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void debug(Marker marker, String s, Object o, Object o1) {
    logger.debug(
        marker, maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void debug(Marker marker, String s, Object... objects) {
    logger.debug(marker, maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void debug(Marker marker, String s, Throwable throwable) {
    logger.debug(marker, maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void error(String s) {
    logger.error(maskSensitiveFields(s));
  }

  @Override
  public void error(String s, Object o) {
    logger.error(maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void error(String s, Object o, Object o1) {
    logger.error(maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void error(String s, Object... objects) {
    logger.error(maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void error(String s, Throwable throwable) {
    logger.error(maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled(marker);
  }

  @Override
  public void error(Marker marker, String s) {
    logger.error(marker, maskSensitiveFields(s));
  }

  @Override
  public void error(Marker marker, String s, Object o) {
    logger.error(marker, maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void error(Marker marker, String s, Object o, Object o1) {
    logger.error(
        marker, maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void error(Marker marker, String s, Object... objects) {
    logger.error(marker, maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void error(Marker marker, String s, Throwable throwable) {
    logger.error(marker, maskSensitiveFields(s), throwable);
  }

  @Override
  public void warn(String s) {
    logger.warn(maskSensitiveFields(s));
  }

  @Override
  public void warn(String s, Object o) {
    logger.warn(maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void warn(String s, Object o, Object o1) {
    logger.warn(maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void warn(String s, Object... objects) {
    logger.warn(maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void warn(String s, Throwable throwable) {
    logger.warn(maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled(marker);
  }

  @Override
  public void warn(Marker marker, String s) {
    logger.warn(marker, maskSensitiveFields(s));
  }

  @Override
  public void warn(Marker marker, String s, Object o) {
    logger.warn(marker, maskSensitiveFields(s), filterSensitiveFields(o));
  }

  @Override
  public void warn(Marker marker, String s, Object o, Object o1) {
    logger.warn(
        marker, maskSensitiveFields(s), filterSensitiveFields(o), filterSensitiveFields(o1));
  }

  @Override
  public void warn(Marker marker, String s, Object... objects) {
    logger.warn(marker, maskSensitiveFields(s), filterSensitiveFields(objects));
  }

  @Override
  public void warn(Marker marker, String s, Throwable throwable) {
    logger.warn(marker, maskSensitiveFields(s), throwable);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }
}
