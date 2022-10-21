package com.github.giovannilamarmora.utils.exception;

import com.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import com.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

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
  public static final HttpStatus DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

  public final Logger LOG = LoggerFactory.getLogger(this.getClass());

  public ExceptionResponse error = new ExceptionResponse();

  @PostConstruct
  void setupResponse() {
    error.getError().setExceptionCode(INTERNAL_SERVER_ERROR);
    error.getError().setStatus(DEFAULT_STATUS);
    error.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    error.getError().setMessage("An error is occurred");
  }

  @ExceptionHandler(value = UtilsException.class)
  private ResponseEntity<ExceptionResponse> handleUtilsException(
      UtilsException e, HttpServletRequest request) {

    LOG.error(
        "An error happened while calling {} Downstream API: {}",
        request.getRequestURI(),
        e.getMessage());
    if (e.exceptionCode != null) {
      error
          .getError()
          .setExceptionCode(
              !e.exceptionCode.name().isBlank()
                  ? e.exceptionCode.name()
                  : error.getError().getExceptionCode());
      error
          .getError()
          .setStatus(
              e.exceptionCode.getStatus() != null ? e.exceptionCode.getStatus() : DEFAULT_STATUS);
      error.setUrl(request.getRequestURI().isBlank() ? null : request.getRequestURI());
      if (!e.exceptionCode.getMessage().isBlank() && !e.getMessage().isBlank()) {
        error
            .getError()
            .setMessage(e.exceptionCode.getMessage() + " | Exception Message: " + e.getMessage());
      } else if (!e.exceptionCode.getMessage().isBlank()) {
        error.getError().setMessage(e.exceptionCode.getMessage());
      } else error.getError().setMessage(e.getMessage());
      error
          .getError()
          .setExceptionName(
              e.exceptionCode.exceptionName().isBlank() ? null : e.exceptionCode.exceptionName());
      error.setCorrelationId(CorrelationIdUtils.getCorrelationId());
      return new ResponseEntity<>(error, e.exceptionCode.getStatus());
    } else {
      return new ResponseEntity<>(error, DEFAULT_STATUS);
    }
  }

  @ExceptionHandler(value = Exception.class)
  private ResponseEntity<ExceptionResponse> handleException(
      Exception e, HttpServletRequest request) {
    LOG.error(
        "An error happened while calling {} Downstream API: {}",
        request.getRequestURI(),
        e.getMessage());
    if (e != null) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      error = getExceptionResponse(e, request, GenericException.ERRDEFUTL001, status);
      return new ResponseEntity<>(error, status);
    } else {
      return new ResponseEntity<>(error, DEFAULT_STATUS);
    }
  }

  public static ExceptionResponse getExceptionResponse(
      Exception e, HttpServletRequest request, ExceptionCode exceptionCode, HttpStatus status) {
    ExceptionResponse exceptionResponse = new ExceptionResponse();
    HttpStatus defaultStatus = HttpStatus.BAD_REQUEST;
    exceptionResponse
        .getError()
        .setExceptionName(e.getClass().getName().isBlank() ? null : e.getClass().getSimpleName());
    exceptionResponse
        .getError()
        .setExceptionCode(
            exceptionCode != null && exceptionCode.name() != null && !exceptionCode.name().isBlank()
                ? exceptionCode.name()
                : GenericException.ERRDEFUTL001.name());
    exceptionResponse.getError().setStatus(status != null ? status : defaultStatus);
    if (e.getMessage() != null && !e.getMessage().isBlank()) {
      exceptionResponse.getError().setMessage("Exception Message: " + e.getMessage());
    } else exceptionResponse.getError().setMessage(null);
    exceptionResponse.setUrl(
        request.getRequestURI() == null || request.getRequestURI().isBlank()
            ? null
            : request.getRequestURI());
    exceptionResponse.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    return exceptionResponse;
  }
}
