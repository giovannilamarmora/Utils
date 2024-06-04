package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Logged
public class Utilities {

  private static final Logger LOG = LoggerFilter.getLogger(Utilities.class);

  private static final ObjectMapper objectMapper =
      MapperUtils.mapper()
          .enableJavaTime()
          .disableDateAsTimestamp()
          .emptyStringAsNullObject()
          .failOnEmptyBean()
          .build();

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String convertObjectToJson(Object object) {
    LOG.debug("Converting Object");
    if (ObjectUtils.isEmpty(object)) {
      LOG.debug("The Object is null, returning null");
      return null;
    }
    String value = null;
    try {
      value = objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to write json!");
    }
    LOG.debug("The Object is converted, value is {}", value);
    return value;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String convertMapToString(Map<String, ?> map) {
    LOG.debug("Converting Map");
    if (ObjectUtils.isEmpty(map)) {
      LOG.debug("The Map is null, returning null");
      return null;
    }
    String mapAsString =
        map.keySet().stream()
            .map(key -> "\"" + key + "\"" + ": \"" + map.get(key) + "\"")
            .collect(Collectors.joining(", ", "{", "}"));
    LOG.debug("The Map is converted, value is {}", mapAsString);
    return mapAsString;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String getByteArrayFromImageURL(String url) {
    LOG.debug("Converting Image URL");
    if (ObjectUtils.isEmpty(url)) return null;
    if (url.contains("https://")) {
      try {
        URL imageUrl = new URL(url);
        URLConnection ucon = imageUrl.openConnection();
        InputStream is = ucon.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = is.read(buffer, 0, buffer.length)) != -1) {
          baos.write(buffer, 0, read);
        }
        baos.flush();
        String encoded =
            // Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT).replaceAll("\n", "");
            Base64.getEncoder().encodeToString(baos.toByteArray()).replaceAll("\n", "");
        LOG.debug("Returning URL data:image/png;base64");
        return "data:image/png;base64," + encoded;
      } catch (Exception e) {
        LOG.debug("An Exception occurred during the conversation, url returned is {}", url);
        return url;
      }
    } else {
      LOG.debug("Not contains https://, returning data:image/png;base64,");
      return url;
    }
  }

  public static <T> boolean isInstanceOf(String source, TypeReference<T> typeReference) {
    try {
      return !isNullOrEmpty(objectMapper.readValue(source, typeReference));
    } catch (JsonProcessingException e) {
      return false;
    }
  }

  public static boolean isNullOrEmpty(Object obj) {
    if (ObjectUtils.isEmpty(obj)) return true;
    // Ottiene tutti i campi della classe dell'oggetto
    Field[] campi = obj.getClass().getDeclaredFields();
    // Itera su tutti i campi
    for (Field campo : campi) {
      campo.setAccessible(true); // Permette l'accesso ai campi privati
      try {
        // Controlla se il campo è null
        if (campo.get(obj) != null) {
          return false; // Se anche solo un campo non è null, restituisce false
        }
      } catch (IllegalAccessException e) {
        return true;
      }
    }
    return true; // Se tutti i campi sono null, restituisce true
  }

  public static boolean isCharacterAndRegexValid(String field, String regex) {
    if (ObjectUtils.isEmpty(field) || ObjectUtils.isEmpty(regex)) return false;
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(field);
    return m.find();
  }
}
