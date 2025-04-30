package io.github.giovannilamarmora.utils.logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ObjectSanitizer {

  private final Pattern sensitiveFieldPattern;
  private final SensitiveDataMasker masker;

  public ObjectSanitizer(String sensitiveRegex) {
    this.sensitiveFieldPattern = Pattern.compile("(?i)(" + sensitiveRegex + ")");
    this.masker = new SensitiveDataMasker(sensitiveRegex);
  }

  public Object[] sanitize(Object... objects) {
    List<Object> sanitized = new ArrayList<>();
    for (Object obj : objects) {
      if (obj instanceof String str) {
        sanitized.add(masker.mask(str));
      } else if (obj != null && !isJavaBaseClass(obj.getClass())) {
        sanitized.add(cloneAndMask(obj));
      } else {
        sanitized.add(obj);
      }
    }
    return sanitized.toArray();
  }

  private boolean isJavaBaseClass(Class<?> clazz) {
    Module module = clazz.getModule();
    return module != null && "java.base".equals(module.getName());
  }

  private Object cloneAndMask(Object original) {
    try {
      Object clone = original.getClass().getDeclaredConstructor().newInstance();
      for (Field field : original.getClass().getDeclaredFields()) {
        field.setAccessible(true);
        Object value = field.get(original);
        if (value instanceof String && isSensitive(field.getName())) {
          field.set(clone, "********");
        } else {
          field.set(clone, value);
        }
      }
      return clone;
    } catch (Exception e) {
      return original;
    }
  }

  private boolean isSensitive(String fieldName) {
    return sensitiveFieldPattern.matcher(fieldName).matches();
  }
}
