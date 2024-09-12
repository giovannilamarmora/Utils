package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.annotation.JsonCreator;

public interface EnumWithValue {
  String getValue();

  @JsonCreator
  static <E extends Enum<E> & EnumWithValue> E fromValue(Class<E> enumType, String value) {
    for (E enumConstant : enumType.getEnumConstants()) {
      if (enumConstant.getValue().equals(value)) {
        return enumConstant;
      }
    }
    throw new IllegalArgumentException(
        "No enum constant " + enumType.getCanonicalName() + " with value " + value);
  }
}
