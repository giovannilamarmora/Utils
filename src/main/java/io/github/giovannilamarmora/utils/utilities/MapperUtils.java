package io.github.giovannilamarmora.utils.utilities;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Logged
public class MapperUtils {

  private static final Logger LOG = LoggerFilter.getLogger(MapperUtils.class);

  public static MapperBuilder mapper() {
    return new MapperBuilder();
  }

  public static class MapperBuilder {
    private final ObjectMapper objectMapper;

    public MapperBuilder() {
      this.objectMapper =
          new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());
    }

    public MapperBuilder dateAsTimestamp() {
      this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
      return this;
    }

    public MapperBuilder emptyStringAsNullObject() {
      this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
      return this;
    }

    public MapperBuilder failOnEmptyBean() {
      this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
      return this;
    }

    public MapperBuilder indentOutput() {
      this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      return this;
    }

    public ObjectMapper build() {
      return this.objectMapper;
    }
  }
}
