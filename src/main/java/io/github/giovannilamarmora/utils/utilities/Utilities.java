package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Logged
public class Utilities {

  private static final Logger LOG = LoggerFilter.getLogger(Utilities.class);

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

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static Map<String, String> getFinalMapFromValue(
      Map<String, String> source, Map<String, String> target) {
    // Creazione della mappa finalParam
    Map<String, String> finalParam = new HashMap<>();
    Pattern pattern = Pattern.compile("\\{\\{(.+?)\\}\\}");

    for (Map.Entry<String, String> entry : target.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      Matcher matcher = pattern.matcher(value);

      StringBuffer result = new StringBuffer();
      while (matcher.find()) {
        String placeholder = matcher.group(1);
        String replacement = source.getOrDefault(placeholder, matcher.group(0));
        if (!ObjectUtils.isEmpty(replacement)) matcher.appendReplacement(result, replacement);
      }
      matcher.appendTail(result);

      finalParam.put(key, result.toString());
    }
    return finalParam;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static <T> boolean isInstanceOf(String source, TypeReference<T> typeReference) {
    return !isNullOrEmpty(Mapper.readObject(source, typeReference));
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
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

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static boolean isCharacterAndRegexValid(String field, String regex) {
    if (ObjectUtils.isEmpty(field) || ObjectUtils.isEmpty(regex)) return false;
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(field);
    return m.find();
  }
}
