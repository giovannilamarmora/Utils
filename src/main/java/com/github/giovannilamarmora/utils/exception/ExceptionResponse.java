package com.github.giovannilamarmora.utils.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

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
  private String exceptionCode;
  private String exceptionName;
  private HttpStatus status;
  private String correlationId;
  private String message;

  public ExceptionResponse(
          String exceptionCode, String exceptionName, HttpStatus status, String correlationId, String message) {
    this.dateTime = getDateTime();
    this.exceptionCode = exceptionCode;
    this.exceptionName = exceptionName;
    this.status = status;
    this.correlationId = correlationId;
    this.message = message;
  }

  public LocalDateTime getDateTime() {
    return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }
}
