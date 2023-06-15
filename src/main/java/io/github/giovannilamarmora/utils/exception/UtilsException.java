package io.github.giovannilamarmora.utils.exception;

import io.github.giovannilamarmora.utils.exception.dto.ErrorInfo;
import io.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import io.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Getter
@ControllerAdvice
public class UtilsException extends RuntimeException {
  private ExceptionCode exceptionCode;
  private String exceptionMessage;

  private String errorStackTrace;
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

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public UtilsException(ExceptionCode exceptionCode, String message, String exceptionMessage) {
    super(message);
    this.exceptionCode = exceptionCode;
    this.exceptionMessage = exceptionMessage;
  }

  /**
   * Constructs a new exception with the stacktrace detail message. The cause is not initialized,
   * and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public UtilsException(
      ExceptionCode exceptionCode,
      String message,
      String exceptionMessage,
      String errorStackTrace) {
    super(message);
    this.exceptionCode = exceptionCode;
    this.exceptionMessage = exceptionMessage;
    this.errorStackTrace = errorStackTrace;
  }

  public final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler(value = UtilsException.class)
  private ResponseEntity<ExceptionResponse> handleUtilsException(
      UtilsException e, HttpServletRequest request) {
    ExceptionResponse error = defaultResponse();
    LOG.error(
        "[UtilsException Handler] An error happened while calling {} Downstream API: {}",
        request.getRequestURI(),
        e.getMessage());
    if (e.exceptionCode != null) {
      ErrorInfo errorMes = new ErrorInfo();
      errorMes.setExceptionCode(e.exceptionCode.name());
      errorMes.setStatus(
          e.exceptionCode.getStatus() != null
              ? e.exceptionCode.getStatus()
              : GenericException.ERR_DEF_UTL_001.getStatus());
      error.setUrl(request.getRequestURI());

      if (e.getMessage() != null && !e.getMessage().isBlank()) {
        errorMes.setMessage(e.getMessage());
      } else errorMes.setMessage(e.exceptionCode.getMessage());

      if (e.getExceptionMessage() != null && !e.getExceptionMessage().isBlank())
        errorMes.setExceptionMessage(e.getExceptionMessage());

      if (e.getStackTrace() != null && e.getStackTrace().length != 0) {
        errorMes.setStackTrace(Arrays.toString(e.getStackTrace()));
        LOG.error(Arrays.toString(e.getStackTrace()));
      }

      errorMes.setExceptionName(
          e.exceptionCode.exceptionName().isBlank() ? null : e.exceptionCode.exceptionName());
      error.setCorrelationId(CorrelationIdUtils.getCorrelationId());
      error.setError(errorMes);

      return new ResponseEntity<>(error, e.exceptionCode.getStatus());
    } else {
      LOG.error(Arrays.toString(e.getStackTrace()));
      return new ResponseEntity<>(error, GenericException.ERR_DEF_UTL_001.getStatus());
    }
  }

  @ExceptionHandler(value = Exception.class)
  private ResponseEntity<ExceptionResponse> handleException(
      Exception e, HttpServletRequest request) {
    ExceptionResponse error = defaultResponse();
    if (e != null) {
      LOG.error(
          "[Exception Handler] An error happened while calling {} Downstream API: {}",
          request.getRequestURI(),
          e.getMessage());

      HttpStatus status = HttpStatus.BAD_REQUEST;
      error = getExceptionResponse(e, request, GenericException.ERR_DEF_UTL_001, status);
      if (e.getStackTrace().length != 0) {
        error.getError().setStackTrace(Arrays.toString(e.getStackTrace()));
        LOG.error(Arrays.toString(e.getStackTrace()));
      }
      return new ResponseEntity<>(error, status);
    } else {
      return new ResponseEntity<>(error, GenericException.ERR_DEF_UTL_001.getStatus());
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
            : GenericException.ERR_DEF_UTL_001.name());
    errorMes.setStatus(status != null ? status : defaultStatus);
    if (e.getMessage() != null && !e.getMessage().isBlank()) {
      errorMes.setExceptionMessage(e.getMessage());
    }
    exceptionResponse.setUrl(
        request.getRequestURI() == null || request.getRequestURI().isBlank()
            ? null
            : request.getRequestURI());
    exceptionResponse.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    exceptionResponse.setError(errorMes);
    return exceptionResponse;
  }

  private ExceptionResponse defaultResponse() {
    ExceptionResponse exceptionResponse = new ExceptionResponse();
    ErrorInfo errorMes = new ErrorInfo();
    errorMes.setExceptionCode(GenericException.ERR_DEF_UTL_001.name());
    errorMes.setStatus(GenericException.ERR_DEF_UTL_001.getStatus());
    exceptionResponse.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    errorMes.setExceptionMessage(GenericException.ERR_DEF_UTL_001.getMessage());
    errorMes.setExceptionName(GenericException.ERR_DEF_UTL_001.exceptionName());
    exceptionResponse.setError(errorMes);
    return exceptionResponse;
  }
}
