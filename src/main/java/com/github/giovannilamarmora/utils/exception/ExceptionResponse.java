package com.github.giovannilamarmora.utils.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse implements Serializable {

  private String error;
  private HttpStatus status;
  private String correlationId;
  private String message;

  public ExceptionResponse(String error, HttpStatus status, String correlationId, String message) {
    this.error = error;
    this.correlationId = correlationId;
    this.status = status;
    this.message = message;
  }

  public ExceptionResponse() {}

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public void setStatus(HttpStatus status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }
}
