package io.github.giovannilamarmora.utils.jsonSerialize;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.springframework.util.ObjectUtils;

public class LowerCaseConverter extends StdConverter<String, String> {
  @Override
  public String convert(String value) {
    if (ObjectUtils.isEmpty(value)) return null;
    return value.toLowerCase();
  }
}
