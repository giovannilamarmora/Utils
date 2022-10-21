package com.github.giovannilamarmora.utils.exception;

import com.github.giovannilamarmora.utils.exception.dto.ErrorInfo;
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

  public final Logger LOG = LoggerFactory.getLogger(this.getClass());

  private final ErrorInfo errorInfo = new ErrorInfo();
  public ExceptionResponse error = new ExceptionResponse(errorInfo);

  @PostConstruct
  void setupResponse() {
    errorInfo.setExceptionCode(GenericException.ERRDEFUTL001.name());
    error.getError().setStatus(GenericException.ERRDEFUTL001.getStatus());
    error.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    errorInfo.setMessage(GenericException.ERRDEFUTL001.getMessage());
    errorInfo.setExceptionName(GenericException.ERRDEFUTL001.exceptionName());
  }

  @ExceptionHandler(value = UtilsException.class)
  private ResponseEntity<ExceptionResponse> handleUtilsException(
      UtilsException e, HttpServletRequest request) {

    LOG.error(
        "An error happened while calling {} Downstream API: {}",
        request.getRequestURI(),
        e.getMessage());
    if (e.exceptionCode != null) {
      ErrorInfo errorMes = new ErrorInfo();
      errorMes.setExceptionCode(
          !e.exceptionCode.name().isBlank()
              ? e.exceptionCode.name()
              : GenericException.ERRDEFUTL001.name());
      errorMes.setStatus(
          e.exceptionCode.getStatus() != null
              ? e.exceptionCode.getStatus()
              : GenericException.ERRDEFUTL001.getStatus());
      error.setUrl(request.getRequestURI().isBlank() ? null : request.getRequestURI());
      if (e.exceptionCode.getMessage() != null
          && e.getMessage() != null
          && !e.exceptionCode.getMessage().isBlank()
          && !e.getMessage().isBlank()) {
        errorMes.setMessage(
            "Message: " + e.exceptionCode.getMessage() + " | Exception Message: " + e.getMessage());
      } else if (e.exceptionCode.getMessage() != null
              && !e.exceptionCode.getMessage().isBlank()
              && e.getMessage() == null
          || e.getMessage().isBlank()) {
        errorMes.setMessage(e.exceptionCode.getMessage());
      } else errorMes.setMessage(e.getMessage());
      errorMes.setExceptionName(
          e.exceptionCode.exceptionName().isBlank() ? null : e.exceptionCode.exceptionName());
      error.setCorrelationId(CorrelationIdUtils.getCorrelationId());
      error.setError(errorMes);
      return new ResponseEntity<>(error, e.exceptionCode.getStatus());
    } else {
      return new ResponseEntity<>(error, GenericException.ERRDEFUTL001.getStatus());
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
      return new ResponseEntity<>(error, GenericException.ERRDEFUTL001.getStatus());
    }
  }

  public static ExceptionResponse getExceptionResponse(
      Exception e, HttpServletRequest request, ExceptionCode exceptionCode, HttpStatus status) {
    ExceptionResponse exceptionResponse = new ExceptionResponse();
    ErrorInfo errorMes = new ErrorInfo();
    HttpStatus defaultStatus = HttpStatus.BAD_REQUEST;
    errorMes.setExceptionName(
        e.getClass().getName().isBlank() ? null : e.getClass().getSimpleName());
    errorMes.setExceptionCode(
        exceptionCode != null && exceptionCode.name() != null && !exceptionCode.name().isBlank()
            ? exceptionCode.name()
            : GenericException.ERRDEFUTL001.name());
    errorMes.setStatus(status != null ? status : defaultStatus);
    if (e.getMessage() != null && !e.getMessage().isBlank()) {
      errorMes.setMessage("Exception Message: " + e.getMessage());
    } else errorMes.setMessage(null);
    exceptionResponse.setUrl(
        request.getRequestURI() == null || request.getRequestURI().isBlank()
            ? null
            : request.getRequestURI());
    exceptionResponse.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    exceptionResponse.setError(errorMes);
    return exceptionResponse;
  }
}
