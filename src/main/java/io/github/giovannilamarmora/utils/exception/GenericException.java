package io.github.giovannilamarmora.utils.exception;

import org.springframework.http.HttpStatus;

enum GenericException implements ExceptionCode {
  ERRDEFUTL001("EXCEPTION_HANDLER", HttpStatus.BAD_REQUEST, "Caught Exception with handler");

  private final HttpStatus status;
  private final String message;
  private final String exceptionName;

  GenericException(String exceptionName, HttpStatus status, String message) {
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
