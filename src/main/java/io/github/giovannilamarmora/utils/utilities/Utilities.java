package io.github.giovannilamarmora.utils.utilities;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
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
  public static boolean isCharacterAndRegexValid(String field, String regex) {
    if (ObjectUtils.isEmpty(field) || ObjectUtils.isEmpty(regex)) return false;
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(field);
    return m.find();
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static Long convertToSize(String size) {
    if (ObjectToolkit.isNullOrEmpty(size)) {
      throw new IllegalArgumentException("Size must not be null or empty");
    }

    String unit = size.replaceAll("[0-9]", "").trim().toUpperCase();
    long value = Long.parseLong(size.replaceAll("[^0-9]", "").trim());

    return switch (unit) {
      case "KB" -> value * 1024; // 1 KB = 1024 bytes
      case "MB" -> value * 1024 * 1024; // 1 MB = 1024 * 1024 bytes
      case "GB" -> value * 1024 * 1024 * 1024; // 1 GB = 1024 * 1024 * 1024 bytes
      case "B" -> value; // Already in bytes
      default -> throw new IllegalArgumentException("Unsupported size unit: " + unit);
    };
  }
}
