package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Logged
public class Utilities {

  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

  private static final ObjectMapper objectMapper =
      new ObjectMapper()
          .findAndRegisterModules()
          .registerModule(new JavaTimeModule())
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String convertObjectToJson(Object object) throws JsonProcessingException {
    LOG.debug("Converting Object");
    if (ObjectUtils.isEmpty(object)) {
      LOG.debug("The Object is null, returning null");
      return null;
    }
    String value = objectMapper.writeValueAsString(object);
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
    LOG.debug("Converting URL {}", url);
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
        LOG.debug("Returning URL data:image/png;base64{}", encoded);
        return "data:image/png;base64," + encoded;
      } catch (Exception e) {
        LOG.debug("An Exception occurred during the conversation, url returned is {}", url);
        return url;
      }
    } else {
      LOG.debug("Not contains https://, returning {}", url);
      return url;
    }
  }
}
