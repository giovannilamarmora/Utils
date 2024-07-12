package io.github.giovannilamarmora.utils.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContextConfig {
  TRACE_ID("Trace-ID"),
  SPAN_ID("Span-ID"),
  PARENT_ID("Parent-ID"),
  ENV("environment");

  private final String value;
}
