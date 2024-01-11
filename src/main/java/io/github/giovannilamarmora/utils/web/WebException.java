package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class WebException extends UtilsException {
  private static final ExceptionCode DEFAULT_ERROR = GenericException.ERR_EXC_WEB_001;

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public WebException(String message) {
    super(DEFAULT_ERROR, message);
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param exceptionCode
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public WebException(ExceptionCode exceptionCode, String message) {
    super(exceptionCode, message);
  }
}
