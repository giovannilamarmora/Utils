package io.github.giovannilamarmora.utils.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse implements Serializable {

  private LocalDateTime dateTime;
  private String url;
  private String correlationId;
  private ErrorInfo error;

  public ExceptionResponse(ErrorInfo error) {
    this.dateTime = getDateTime();
    this.error = error;
  }

  public LocalDateTime getDateTime() {
    return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
