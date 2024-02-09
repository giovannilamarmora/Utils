package io.github.giovannilamarmora.utils.utilities;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
  public static String searchFileFromResources(String fileName, ResourceLoader resourceLoader)
      throws IOException {
    Path path = getResourcePath(fileName, resourceLoader);
    return path != null ? new String(java.nio.file.Files.readAllBytes(path)) : null;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static Path getResourcePath(String fileName, ResourceLoader resourceLoader)
      throws IOException {
    Resource resource = resourceLoader.getResource("classpath:/");

    LOG.debug("The Resource URI is {}", resource.getURI());

    Path resourcesPath = null;

    if (resource.getURI().getScheme().equals("jar")) {
      //URL resourceUrl = FilesUtils.class.getProtectionDomain().getCodeSource().getLocation();
      String path = resource.getURI().getPath();
      if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
      String jarPath = path.substring(5, path.indexOf("!"));

      try (JarFile jarFile = new JarFile(jarPath)) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          if (!entry.isDirectory() && entry.getName().endsWith(fileName)) {
            // Found the entry matching the folder and file name
            try (InputStream entryStream = jarFile.getInputStream(entry)) {
              Path tempFile = java.nio.file.Files.createTempFile("jar-entry", null);
              java.nio.file.Files.copy(entryStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
              LOG.debug(
                  "The Resource Path for scheme {} is {}", resource.getURI().getScheme(), tempFile);
              return tempFile;
            }
          }
        }
      }
    } else {
      resourcesPath = Path.of(resource.getURI());
      LOG.debug(
          "The Resource Path for scheme {} is {}",
          resource.getURI().getScheme(),
          resourcesPath.toUri());

      try (Stream<Path> paths = java.nio.file.Files.walk(resourcesPath)) {
        Predicate<Path> validatePath =
            path -> path != null && path.toFile().isFile() && path.toString().endsWith(fileName);
        return paths.filter(validatePath).findFirst().orElse(null);
      }
    }
    LOG.warn("The Resource Path was found");
    return resourcesPath;
  }
}
