package io.github.giovannilamarmora.utils.utilities;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Logged
public class FilesUtils {
  private static final Logger LOG = LoggerFactory.getLogger(FilesUtils.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static boolean matchPath(String requestPath, String endpoint) {
    String[] requestPathSegments = requestPath.split("/");
    String[] endpointSegments = endpoint.split("/");

    if (requestPathSegments.length != endpointSegments.length) {
      return false;
    }
    for (int i = 0; i < endpointSegments.length; i++) {
      if (!endpointSegments[i].equals("**")
          && !endpointSegments[i].equals(requestPathSegments[i])) {
        return false;
      }
    }
    return true;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static String searchFileFromResources(String fileName)
      throws IOException, URISyntaxException {
    Path path = getResourcePath(fileName);
    return path != null ? new String(Files.readAllBytes(path)) : null;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static Path getResourcePath(String fileName) throws IOException, URISyntaxException {
    // Resource resource = resourceLoader.getResource("classpath:/");
    URL resourceUrl = FilesUtils.class.getProtectionDomain().getCodeSource().getLocation();

    LOG.debug("The Resource URI is {}", resourceUrl);

    Path resourcesPath = null;

    // if (resource.getURI().getScheme().equals("jar")) {
    if (resourceUrl.getProtocol().equals("jar")) {
      LOG.debug("JAR file detected");
      // URL resourceUrl = AppConfig.class.getProtectionDomain().getCodeSource().getLocation();
      String path = resourceUrl.getPath();
      // String path = resource.getURI().getPath();
      if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
      String jarPath = path.substring(5, path.indexOf("!"));

      try (JarFile jarFile = new JarFile(jarPath)) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          if (!entry.isDirectory() && entry.getName().endsWith(fileName)) {
            // Found the entry matching the folder and file name
            try (InputStream entryStream = jarFile.getInputStream(entry)) {
              Path tempFile = Files.createTempFile("jar-entry", null);
              java.nio.file.Files.copy(entryStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
              LOG.debug(
                  "The Resource Path for scheme {} is {}", resourceUrl.getProtocol(), tempFile);
              return tempFile;
            }
          }
        }
      }
    } else {
      resourcesPath = Path.of(resourceUrl.toURI());
      LOG.debug(
          "The Resource Path for scheme {} is {}",
          resourceUrl.getProtocol(),
          resourcesPath.toUri());

      try (Stream<Path> paths = Files.walk(resourcesPath)) {
        Predicate<Path> validatePath =
            path -> path != null && path.toFile().isFile() && path.toString().endsWith(fileName);
        return paths.filter(validatePath).findFirst().orElse(null);
      }
    }
    LOG.warn("The Resource Path was found");
    return resourcesPath;
  }
}
