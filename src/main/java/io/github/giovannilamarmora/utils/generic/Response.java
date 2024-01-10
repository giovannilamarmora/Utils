package io.github.giovannilamarmora.utils.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
  private Integer status = HttpStatus.OK.value();
  private String message;
  private String correlationId = CorrelationIdUtils.getCorrelationId();
  private Object data;
}
