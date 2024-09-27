package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;

public interface Mapper {

  Logger LOG = LoggerFilter.getLogger(Utilities.class);

  ObjectMapper objectMapper =
      MapperUtils.mapper()
          .enableJavaTime()
          .disableDateAsTimestamp()
          .emptyStringAsNullObject()
          .failOnEmptyBean()
          .build();

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String writeObjectToString(Object object) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to write json!");
    }
  }

  /**
   * @param object The object to convert.
   * @param tClass The class indicating the target type.
   * @param <T> The type of the desired object.
   * @return The converted object.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> T convertValue(T object, Class<T> tClass) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    try {
      return objectMapper.convertValue(object, tClass);
    } catch (IllegalArgumentException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to convert value!");
    }
  }

  /**
   * Converts an object to the specified type.
   *
   * @param object The object to convert.
   * @param typeReference The type reference indicating the target type.
   * @param <T> The type of the desired object.
   * @return The converted object.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> T convertValue(T object, TypeReference<T> typeReference) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    try {
      return objectMapper.convertValue(object, typeReference);
    } catch (IllegalArgumentException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to convert json!");
    }
  }

  /**
   * @param object The object to convert.
   * @param tClass The class indicating the target type.
   * @param <T> The type of the desired object.
   * @return The converted object.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> T convertObject(Object object, Class<T> tClass) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    try {
      return objectMapper.convertValue(object, tClass);
    } catch (IllegalArgumentException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to convert value!");
    }
  }

  /**
   * Converts an object to the specified type.
   *
   * @param object The object to convert.
   * @param typeReference The type reference indicating the target type.
   * @param <T> The type of the desired object.
   * @return The converted object.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> T convertObject(Object object, TypeReference<T> typeReference) {
    if (ObjectUtils.isEmpty(object)) {
      return null;
    }
    try {
      return objectMapper.convertValue(object, typeReference);
    } catch (IllegalArgumentException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to convert json!");
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> T readObject(String objectString, Class<T> tClass) {
    if (ObjectUtils.isEmpty(objectString)) {
      return null;
    }
    try {
      return objectMapper.readValue(objectString, tClass);
    } catch (JsonProcessingException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to read value!");
    }
  }

  /**
   * Reads a string to the specified type.
   *
   * @param objectString The object to convert.
   * @param typeReference The type reference indicating the target type.
   * @param <T> The type of the desired object.
   * @return The converted object.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static <T> T readObject(String objectString, TypeReference<T> typeReference) {
    if (ObjectUtils.isEmpty(objectString)) {
      return null;
    }
    try {
      return objectMapper.readValue(objectString, typeReference);
    } catch (JsonProcessingException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to read json!");
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static JsonNode readTree(String objectString) {
    if (ObjectUtils.isEmpty(objectString)) {
      return null;
    }
    try {
      return objectMapper.readTree(objectString);
    } catch (JsonProcessingException e) {
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to read value!");
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static JsonNode readTree(Map<String, Object> objectMap) {
    if (objectMap == null || objectMap.isEmpty()) {
      return null;
    } else {
      try {
        return objectMapper.valueToTree(objectMap); // Converte la mappa in JsonNode
      } catch (Exception e) {
        throw new UtilsException(GenericException.ERR_DEF_UTL_001, "Unable to read value!");
      }
    }
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static JsonNode removeField(JsonNode originalNode, String fieldName) {
    if (originalNode.isObject()) {
      ObjectNode newNode = (ObjectNode) originalNode.deepCopy(); // Crea una copia profonda
      newNode.remove(fieldName); // Rimuove il campo specificato
      return newNode; // Restituisce il nuovo JsonNode senza il campo
    }
    return originalNode; // Restituisce l'originale se non è un oggetto
  }

  /**
   * Method to convert a Map to String. Be careful on the use, could not work properly. In that case
   * you should use convertObjectToString.
   *
   * @param map Map to convert
   * @return Map as String
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String convertMapToString(Map<String, ?> map) {
    if (ObjectUtils.isEmpty(map)) {
      return null;
    }
    return map.keySet().stream()
        .map(key -> "\"" + key + "\"" + ": \"" + map.get(key) + "\"")
        .collect(Collectors.joining(", ", "{", "}"));
  }

  /**
   * Removes an element from the map based on the provided key. (Ex. api.be.title)
   *
   * @param map The map from which to remove the element.
   * @param key The nested key of the element to remove.
   * @return The modified map.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static Map<String, Object> deleteKeyFromMap(Map<String, Object> map, String key) {
    String[] keys = key.split("\\.");
    removeNestedKey(map, keys, 0);
    return map;
  }

  /**
   * Removes elements from the map based on a list of nested keys. (Ex.
   * ["api.be.title","api.be.subtitle"])
   *
   * @param map The map from which to remove the elements.
   * @param keys The list of nested keys.
   * @return The modified map.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static Map<String, Object> deleteKeysFromMap(Map<String, Object> map, List<String> keys) {
    for (String key : keys) {
      String[] keyParts = key.split("\\.");
      removeNestedKey(map, keyParts, 0);
    }
    return map;
  }

  /**
   * Recursive method to remove a nested key.
   *
   * @param map The current map.
   * @param keys The list of keys.
   * @param index The index of the current key in the list.
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  private static void removeNestedKey(Map<String, Object> map, String[] keys, int index) {
    if (index == keys.length - 1) {
      map.remove(keys[index]);
      return;
    }

    Object nextLevel = map.get(keys[index]);
    if (nextLevel instanceof Map) {
      removeNestedKey((Map<String, Object>) nextLevel, keys, index + 1);
      // Cleanup of empty maps
      if (((Map<?, ?>) nextLevel).isEmpty()) {
        map.remove(keys[index]);
      }
    }
  }
}
