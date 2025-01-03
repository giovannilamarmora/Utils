package io.github.giovannilamarmora.utils.webClient;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class WebClientException extends UtilsException {

  private static final ExceptionCode DEFAULT_EXCEPTION = GenericException.ERR_WEB_CLI_001;

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public WebClientException(String message) {
    super(DEFAULT_EXCEPTION, message);
  }

  /**
   * Constructs a new runtime exception with the specified detail message, cause, suppression
   * enabled or disabled, and writable stack trace enabled or disabled.
   *
   * @param message the detail message.
   * @param cause the cause. (A {@code null} value is permitted, and indicates that the cause is
   *     nonexistent or unknown.)
   * @since 1.7
   */
  public WebClientException(String message, Throwable cause) {
    super(message, cause, false, false, DEFAULT_EXCEPTION);
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   * @param exceptionMessage
   */
  public WebClientException(String message, String exceptionMessage) {
    super(DEFAULT_EXCEPTION, message, exceptionMessage);
  }
}
