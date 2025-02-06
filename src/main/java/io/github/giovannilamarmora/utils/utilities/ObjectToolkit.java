package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.ObjectUtils;

public interface ObjectToolkit {

  /**
   * Replaces placeholders in the values of the target map with corresponding values from the source
   * map.
   *
   * <p>This method takes two maps: - `source`: a map containing replacement values for
   * placeholders. - `target`: a map whose values may contain placeholders in the format `{{key}}`.
   *
   * <p>Placeholders within the `target` values are replaced with the corresponding values from the
   * `source` map. If a placeholder is not found in `source`, it remains unchanged in the result.
   *
   * @param source the map containing replacement values for placeholders
   * @param target the map whose values may contain placeholders to be replaced
   * @return a new map with `target` values updated based on placeholder replacements
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static Map<String, String> getFinalMapFromValue(
      Map<String, String> source, Map<String, String> target) {
    // Create the final parameter map
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

  /**
   * Checks if all fields of an object are null or empty.
   *
   * <p>This method checks if all fields in the provided object are either null or empty. If any
   * field is not null, the method immediately returns `false`. If all fields are null or empty, it
   * returns `true`.
   *
   * <p>This method accesses private fields through reflection and handles the {@link
   * IllegalAccessException} by returning `true` (indicating an issue accessing the fields).
   *
   * @param obj the object whose fields will be checked
   * @return {@code true} if all fields are null or empty, {@code false} if any field is not null
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static boolean fieldsNullOrEmpty(Object obj) {
    if (ObjectUtils.isEmpty(obj)) {
      return true; // Return true if the object itself is null or empty
    }

    // Get all fields from the object's class
    Field[] fields = obj.getClass().getDeclaredFields();

    // Iterate over the fields
    for (Field field : fields) {
      field.setAccessible(true); // Allow access to private fields
      try {
        // Check if the field value is not null
        if (field.get(obj) != null) {
          return false; // If any field is not null, return false
        }
      } catch (IllegalAccessException e) {
        // Return true if access to a field is not allowed (assumes the field is inaccessible)
        return true;
      }
    }
    return true; // If all fields are null or empty, return true
  }

  /**
   * Checks if the provided source can be deserialized into the specified type and if the resulting
   * object is not empty.
   *
   * <p>This method attempts to deserialize the provided `source` string into an object of the type
   * specified by the `TypeReference`. It then checks if the resulting object has any non-null or
   * non-empty fields using the {@link #fieldsNullOrEmpty(Object)} method. If the object is not
   * empty, it returns {@code true}.
   *
   * <p>This method is useful for validating whether a JSON string (or other serialized data) can be
   * deserialized into a valid object and contains meaningful data.
   *
   * @param source the JSON string or serialized data to be deserialized
   * @param typeReference the type reference indicating the target type to deserialize into
   * @param <T> the type of the object to deserialize into
   * @return {@code true} if the object can be deserialized and is not empty, {@code false} if it is
   *     empty or invalid
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> boolean isInstanceOf(String source, TypeReference<T> typeReference) {
    return !fieldsNullOrEmpty(Mapper.readObject(source, typeReference));
  }

  /**
   * Checks if the provided object is null or empty.
   *
   * <p>This method checks whether the provided object is null or empty. It handles various types of
   * objects, including: - {@link Optional}: returns {@code true} if the {@link Optional} is empty.
   * - {@link CharSequence} (e.g., String): returns {@code true} if the string is empty. - {@link
   * Collection}: returns {@code true} if the collection is empty. - {@link Map}: returns {@code
   * true} if the map is empty. - Arrays: returns {@code true} if the array has no elements.
   *
   * <p>If the object is of a type that is not explicitly checked, it returns {@code false}.
   *
   * @param obj the object to check
   * @return {@code true} if the object is null or empty, {@code false} otherwise
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static boolean isNullOrEmpty(Object obj) {
    if (obj == null) {
      return true; // Return true if the object is null
    }

    return switch (obj) {
      case Optional<?> optional -> optional.isEmpty(); // Check if Optional is empty
      case CharSequence charSequence -> charSequence.isEmpty(); // Check if CharSequence is empty
      case Collection<?> collection -> collection.isEmpty(); // Check if Collection is empty
      case Map<?, ?> map -> map.isEmpty(); // Check if Map is empty
      default -> obj.getClass().isArray() && Array.getLength(obj) == 0; // Check if Array is empty
    };
  }

  /**
   * Checks if the provided string value is a valid value for the specified enum type.
   *
   * <p>This method attempts to match the provided string `value` with a constant in the specified
   * enum class. If the value corresponds to a valid enum constant, the method returns {@code true}.
   * If the value does not match any enum constant or if there is any exception (e.g., invalid
   * input), it returns {@code false}.
   *
   * <p>The method handles both {@link IllegalArgumentException} and {@link NullPointerException}
   * that may occur if the value is not a valid enum constant or if the value is null.
   *
   * @param value the string value to check against the enum constants
   * @param enumClass the enum class that defines the valid constants
   * @param <E> the type of the enum class
   * @return {@code true} if the value is a valid enum constant, {@code false} otherwise
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <E extends Enum<E>> boolean isEnumValue(String value, Class<E> enumClass) {
    try {
      Enum.valueOf(enumClass, value); // Attempt to find the enum constant by name
      return true;
    } catch (IllegalArgumentException | NullPointerException e) {
      return false; // Return false if the value does not match any enum constant
    }
  }

  /**
   * Returns the value of a property if it's not null or empty; otherwise, returns null.
   *
   * <p>This method checks if the provided property is null or empty using {@link
   * #isNullOrEmpty(Object)}. If the property is valid, it returns its value; otherwise, it returns
   * null.
   *
   * @param <T> the type of the object containing the property
   * @param <R> the type of the property to be retrieved
   * @param obj the object containing the property
   * @param getter the function to retrieve the property from the object
   * @return the value of the property, or null if the property is null or empty
   */
  static <T, R> R getValueOrNull(T obj, Function<T, R> getter) {
    R value = getter.apply(obj);
    return isNullOrEmpty(value) ? null : value;
  }

  /**
   * Returns the value of a property if it's not null or empty; otherwise, returns a default value.
   *
   * <p>This method checks if the provided property is null or empty using {@link
   * #isNullOrEmpty(Object)}. If the property is valid, it returns its value; otherwise, it returns
   * the provided default value.
   *
   * @param <T> the type of the object containing the property
   * @param <R> the type of the property to be retrieved
   * @param obj the object containing the property
   * @param getter the function to retrieve the property from the object
   * @param defaultValue the value to return if the property is null or empty
   * @return the value of the property, or the default value if the property is null or empty
   */
  static <T, R> R getValueOrDefault(T obj, Function<T, R> getter, R defaultValue) {
    R value = getter.apply(obj);
    return isNullOrEmpty(value) ? defaultValue : value;
  }

  /**
   * Checks if a nested property is not null or empty at multiple levels.
   *
   * <p>This method checks the given path of properties (from root to leaf) to ensure all are not
   * null and not empty.
   *
   * @param <T> the type of the root object
   * @param <R> the type of the final value to be checked
   * @param obj the root object
   * @param getters the series of getter functions to retrieve the nested values
   * @return {@code true} if all properties along the path are not null or empty, {@code false}
   *     otherwise
   */
  static <T, R> boolean areNotNullOrEmpty(T obj, Function<T, ?>... getters) {
    Object currentValue = obj;
    for (Function getter : getters) {
      currentValue = getter.apply(currentValue);
      if (isNullOrEmpty(currentValue)) {
        return false; // Return false if any value in the path is null or empty
      }
    }
    return true;
  }
}
