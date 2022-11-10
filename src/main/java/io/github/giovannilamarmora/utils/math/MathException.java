package io.github.giovannilamarmora.utils.math;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum MathException implements ExceptionCode {
  ERRMATUTL001(
      "VALUE_NOT_PERMITTED",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "The current value is not permitted");

  private final HttpStatus status;
  private final String message;
  private final String exceptionName;

  MathException(String exceptionName, HttpStatus status, String message) {
    this.exceptionName = exceptionName;
    this.status = status;
    this.message = message;
  }

  /**
   * return the name of the Exception
   *
   * @return String
   */
  @Override
  public String exceptionName() {
    return exceptionName;
  }

  /**
   * Return the message of the Exception
   *
   * @return String
   */
  @Override
  public String getMessage() {
    return message;
  }

  /**
   * Return the HttpStatus of the Exception
   *
   * @return HttpStatus
   */
  @Override
  public HttpStatus getStatus() {
    return status;
  }
}
