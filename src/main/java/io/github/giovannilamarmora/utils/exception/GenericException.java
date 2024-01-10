package io.github.giovannilamarmora.utils.exception;

import org.springframework.http.HttpStatus;

public enum GenericException implements ExceptionCode {
  ERR_EXC_HAN_001("EXCEPTION_HANDLER", HttpStatus.BAD_REQUEST, "Caught Exception with handler"),
  ERR_DEF_UTL_001(
      "UTILS_EXCEPTION_HANDLER", HttpStatus.BAD_REQUEST, "Caught UtilsException with handler"),
  ERR_EXC_UTL_001("UNABLE_TO_READ_THE_FILE", HttpStatus.BAD_REQUEST, "Unable to read the file"),
  ERR_EXC_UTL_002("ERROR_ON_SETTING_FIELD", HttpStatus.BAD_REQUEST, "Error on setting field"),
  ERR_EXC_UTL_003("INVALID_OBJECT_FIELD", HttpStatus.BAD_REQUEST, "Invalid Object field"),
  ERR_MAT_UTL_001(
      "VALUE_NOT_PERMITTED",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "The current value is not permitted"),
  ERR_WEB_CLI_001(
      "WEB_CLIENT_EXCEPTION",
      HttpStatus.INTERNAL_SERVER_ERROR,
      "An error occurred during webClient call, body is ");

  private final HttpStatus status;
  private final String message;
  private final String exception;

  GenericException(String exception, HttpStatus status, String message) {
    this.exception = exception;
    this.status = status;
    this.message = message;
  }

  /**
   * return the name of the Exception
   *
   * @return String
   */
  @Override
  public String exception() {
    return exception;
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
