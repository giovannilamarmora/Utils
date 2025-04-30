package io.github.giovannilamarmora.utils.logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveDataMasker {
  private final List<Pattern> patterns;

  public SensitiveDataMasker(String sensitiveFieldsRegex) {
    this.patterns =
        List.of(
            // 1. JSON "key": "value"
            Pattern.compile(
                "(?i)(\"(" + sensitiveFieldsRegex + ")\"\\s*:\\s*\")(?!\\s*\\*{8,})([^\"]+)(\")"),
            // 2. JSON "key": value senza virgolette
            // Pattern.compile(
            //    "(?i)(\"(" + sensitiveFieldsRegex + ")\"\\s*:\\s*)(?!\\s*\\*{8,})([^\",}\\]]+)"),
            // 3. Header tipo Authorization: Bearer XYZ
            Pattern.compile(
                "(?i)(^|[\\n\\r])([ \\t]*"
                    + sensitiveFieldsRegex
                    + "[ \\t]*[:=][ \\t]*)(?!\\s*\\*{8,})([^\\n\\r]*)"),
            // Pattern.compile(
            //    "(?i)(^|[\\n\\r])([ \\t]*"
            //        + sensitiveFieldsRegex
            //        + "[ \\t]*[:=][ \\t]*)([^\\n\\r]*)"),
            // 4. URL query param tipo ?access_token=XYZ
            Pattern.compile("(?i)([?&](" + sensitiveFieldsRegex + ")=)(?!\\s*\\*{8,})([^&\\s]*)"));
  }

  public String mask(String input) {
    if (input == null) return null;
    String result = input;
    for (Pattern pattern : patterns) {
      Matcher matcher = pattern.matcher(result);
      StringBuilder sb = new StringBuilder();
      while (matcher.find()) {
        int index = patterns.indexOf(pattern);
        if (index == 1) {
          if (!matcher.group(3).equalsIgnoreCase(""))
            matcher.appendReplacement(sb, matcher.group(1) + matcher.group(2) + ": ********");
        } else if (index <= 0) {
          matcher.appendReplacement(sb, "\"" + matcher.group(2) + "\": " + "\"********\"");
        } else {
          matcher.appendReplacement(sb, "********");
        }
      }
      matcher.appendTail(sb);
      result = sb.toString();
    }
    return result;
  }
}
