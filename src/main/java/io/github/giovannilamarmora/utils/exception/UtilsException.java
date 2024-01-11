package io.github.giovannilamarmora.utils.exception;

import io.github.giovannilamarmora.utils.config.UtilsPropertiesManager;
import io.github.giovannilamarmora.utils.exception.dto.ErrorInfo;
import io.github.giovannilamarmora.utils.exception.dto.ExceptionResponse;
import io.github.giovannilamarmora.utils.interceptors.correlationID.CorrelationIdUtils;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Getter
@ControllerAdvice
public class UtilsException extends RuntimeException {
  private ExceptionCode exceptionCode;
  private String exceptionMessage;

  @Autowired private UtilsPropertiesManager propertiesManager;

  public static final Logger LOG = LoggerFactory.getLogger(UtilsException.class);

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
      if (!ObjectUtils.isEmpty(e.exceptionCode.name()))
        errorMes.setErrorCode(e.exceptionCode.name());

      if (!ObjectUtils.isEmpty(e.exceptionCode.getStatus()))
        errorMes.setStatus(e.exceptionCode.getStatus());
      error.setUrl(request.getRequestURI());

      if (!ObjectUtils.isEmpty(e.getMessage())) errorMes.setMessage(e.getMessage());

      if (!ObjectUtils.isEmpty(e.getExceptionMessage()))
        errorMes.setExceptionMessage(e.getExceptionMessage());

      if (propertiesManager.getIsUtilsStackTraceActive()
          && !ObjectUtils.isEmpty(e.getStackTrace())
          && e.getStackTrace().length != 0) {
        errorMes.setStackTrace(Arrays.toString(e.getStackTrace()));
        if (propertiesManager.getIsDebugUtilsStackTraceActive()) LOG.debug("Stacktrace error: ", e);
        else LOG.error("Stacktrace error: ", e);
      } else if (!propertiesManager.getIsUtilsStackTraceActive()
          && !ObjectUtils.isEmpty(e.getStackTrace())
          && e.getStackTrace().length != 0) LOG.debug("Stacktrace error: ", e);

      if (!ObjectUtils.isEmpty(e.exceptionCode.exception()))
        errorMes.setException(e.exceptionCode.exception());
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
    if (!ObjectUtils.isEmpty(e)) {
      LOG.error(
          "[Exception Handler] An error happened while calling {} Downstream API: {}",
          request.getRequestURI(),
          e.getMessage());

      error = getExceptionResponse(e, request, GenericException.ERR_EXC_HAN_001);
    }
    return new ResponseEntity<>(error, GenericException.ERR_EXC_HAN_001.getStatus());
  }

  public static ExceptionResponse getExceptionResponse(
      Exception e, HttpServletRequest request, ExceptionCode exceptionCode) {
    ExceptionResponse exceptionResponse = new ExceptionResponse();
    ErrorInfo errorMes = new ErrorInfo();

    if (!ObjectUtils.isEmpty(e.getClass().getName())) errorMes.setException(e.getClass().getName());

    if (!ObjectUtils.isEmpty(exceptionCode.name())) errorMes.setErrorCode(exceptionCode.name());

    if (!ObjectUtils.isEmpty(exceptionCode.getStatus()))
      errorMes.setStatus(exceptionCode.getStatus());

    if (!ObjectUtils.isEmpty(e.getMessage())) {
      errorMes.setExceptionMessage(e.getMessage());
    }

    if (!ObjectUtils.isEmpty(request.getRequestURI())) {
      exceptionResponse.setUrl(request.getRequestURI());
    }

    if (!ObjectUtils.isEmpty(e.getStackTrace())) {
      errorMes.setStackTrace(Arrays.toString(e.getStackTrace()));
      LOG.error("Stacktrace error: ", e);
    }

    exceptionResponse.setCorrelationId(CorrelationIdUtils.getCorrelationId());
    exceptionResponse.setError(errorMes);
    return exceptionResponse;
  }

  private ExceptionResponse defaultResponse() {
    ErrorInfo errorMes =
        new ErrorInfo(
            GenericException.ERR_DEF_UTL_001.name(),
            GenericException.ERR_DEF_UTL_001.exception(),
            GenericException.ERR_DEF_UTL_001.getStatus(),
            null,
            GenericException.ERR_DEF_UTL_001.getMessage(),
            null);

    return new ExceptionResponse(null, CorrelationIdUtils.getCorrelationId(), errorMes);
  }

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

  public UtilsException(String exceptionMessage, ExceptionCode exceptionCode) {
    this.exceptionCode = exceptionCode;
    this.exceptionMessage = exceptionMessage;
  }

  /**
   * Constructs a new runtime exception with the specified detail message, cause, suppression
   * enabled or disabled, and writable stack trace enabled or disabled.
   *
   * @param message the detail message.
   * @param cause the cause. (A {@code null} value is permitted, and indicates that the cause is
   *     nonexistent or unknown.)
   * @param enableSuppression whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   * @since 1.7
   */
  public UtilsException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      ExceptionCode exceptionCode) {
    super(message, cause, enableSuppression, writableStackTrace);
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
}
