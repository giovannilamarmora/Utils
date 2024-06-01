package io.github.giovannilamarmora.utils.interceptors;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerTest {

  Logger LOG = LoggerFactory.getLogger(this.getClass());
  private static final String PACKAGE_URL = "com.github.giovannilamarmora.utils";

  @Test
  public void logTimeTrackerFailure() {
    LogTimeTracker tracker =
        LogTimeTracker.startInvocation(LogTimeTracker.ActionType.UTILS_LOGGER, "", "");

    Exception e = new Exception();
    tracker.trackFailure(LOG, e);

    Exception e2 = new Exception(e);
    tracker.trackFailure(LOG, e2);

    Exception e3 = new Exception(e2);
    tracker.trackFailure(LOG, e3);
  }

  @Test
  public void checkAllServicesAreWellAnnotatedForLogs() throws Exception {
    List<Class> allClasses = Arrays.asList(getClasses(PACKAGE_URL));

    List<Class> controllers =
        allClasses.stream()
            .filter(c -> c.getName().endsWith("Service"))
            .filter(c -> c.getName().endsWith("Mapper"))
            .collect(Collectors.toList());

    List<Method> methods =
        controllers.stream()
            .flatMap(c -> Arrays.asList(c.getDeclaredMethods()).stream())
            .collect(Collectors.toList());

    List<Method> allPublicMethods =
        methods.stream()
            .filter(m -> m.getModifiers() == Modifier.PUBLIC)
            .collect(Collectors.toList());

    List<Method> notWellAnnotated =
        allPublicMethods.stream()
            .filter(m -> m.getAnnotation(LogInterceptor.class) == null)
            .collect(Collectors.toList());

    Set<Method> allowedNotWellAnnotatedMethods = Set.of();

    List<Method> uncoveredMethods =
        notWellAnnotated.stream()
            .filter(m -> !allowedNotWellAnnotatedMethods.contains(m))
            .collect(Collectors.toList());

    Assertions.assertEquals(uncoveredMethods, List.of());
  }

  // Copied from
  // https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection/22462785
  private static Class[] getClasses(String packageName) throws Exception {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    String path = packageName.replace('.', '/');
    Enumeration<URL> resources = classLoader.getResources(path);
    List<File> dirs = new ArrayList<File>();
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      dirs.add(new File(resource.getFile()));
    }
    ArrayList<Class> classes = new ArrayList<Class>();
    for (File directory : dirs) {
      classes.addAll(findClasses(directory, packageName));
    }
    return classes.toArray(new Class[classes.size()]);
  }

  private static List<Class> findClasses(File directory, String packageName)
      throws ClassNotFoundException {
    List<Class> classes = new ArrayList<Class>();
    if (!directory.exists()) {
      return classes;
    }
    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        classes.add(
            Class.forName(
                packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
      }
    }
    return classes;
  }

  private static Method extractTargetMethod(Class controller, String methodName) {
    List<Method> methods = Arrays.asList(controller.getMethods());
    methods =
        methods.stream().filter(m -> m.getName().equals(methodName)).collect(Collectors.toList());
    Assertions.assertEquals(1, methods.size());
    return methods.get(0);
  }

  private static boolean listEqualsIgnoreOrder(List<String> list1, List<String> list2) {
    return new HashSet<>(list1).equals(new HashSet<>(list2));
  }
}
