package com.github.giovannilamarmora.utils.exception;

import com.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UtilsException extends Exception {
  private ExceptionCode exceptionCode;
  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public UtilsException() {}

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public UtilsException(ExceptionCode exceptionCode) {
    this.exceptionCode = exceptionCode;
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public UtilsException(ExceptionCode exceptionCode, String message) {
    super(message);
    this.exceptionCode = exceptionCode;
  }

  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
  private static final HttpStatus DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

  @ExceptionHandler(value = UtilsException.class)
  public ResponseEntity<ExceptionResponse> exception(UtilsException e) {
    ExceptionResponse error =
        new ExceptionResponse(
            INTERNAL_SERVER_ERROR, DEFAULT_STATUS, CorrelationIdUtils.getCorrelationId(), null);

    if (e.exceptionCode != null) {
      if (!e.exceptionCode.name().isBlank()) error.setError(e.exceptionCode.name());
      if (e.exceptionCode.getStatus() != null) {
        error.setStatus(e.exceptionCode.getStatus());
      } else error.setStatus(DEFAULT_STATUS);
      if (!e.exceptionCode.getMessage().isBlank() && !e.getMessage().isBlank()) {
        error.setMessage(e.exceptionCode.getMessage() + " | Exception Message: " + e.getMessage());
      } else if (!e.exceptionCode.getMessage().isBlank()) {
        error.setMessage(e.exceptionCode.getMessage());
      } else error.setMessage(e.getMessage());
      error.setCorrelationId(CorrelationIdUtils.getCorrelationId());
      return new ResponseEntity<>(error, e.exceptionCode.getStatus());
    } else {
      return new ResponseEntity<>(error, DEFAULT_STATUS);
    }
  }
}
