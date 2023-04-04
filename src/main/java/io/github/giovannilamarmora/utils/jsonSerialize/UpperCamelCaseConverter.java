package io.github.giovannilamarmora.utils.jsonSerialize;

import com.fasterxml.jackson.databind.util.StdConverter;

public class UpperCamelCaseConverter extends StdConverter<String, String> {
  @Override
  public String convert(String value) {
    if (value == null) {
      return null;
    }
    String[] words = value.split("[\\W_]+");
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < words.length; i++) {
      String word =
          words[i].isEmpty()
              ? words[i]
              : Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1).toLowerCase();
      builder.append(word).append(words.length - 1 == i ? "" : " ");
    }
    return builder.toString();
  }
}
