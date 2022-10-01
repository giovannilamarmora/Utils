package com.github.giovannilamarmora.utils.math;

import com.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum MathException implements ExceptionCode {
  VALUE_NOT_PERMITTED(HttpStatus.INTERNAL_SERVER_ERROR, "The current value is not permitted");

  MathException(HttpStatus status, String message) {
  }

  /**
   * Return the message of the Exception
   *
   * @return String
   */
  @Override
  public String getMessage() {
    return null;
  }

  /**
   * Return the HttpStatus of the Exception
   *
   * @return HttpStatus
   */
  @Override
  public HttpStatus getStatus() {
    return null;
  }
}
