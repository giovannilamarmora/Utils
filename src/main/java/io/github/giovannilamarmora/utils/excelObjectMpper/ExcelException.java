package io.github.giovannilamarmora.utils.excelObjectMpper;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum ExcelException implements ExceptionCode {
  ERR_EXC_UTL_001("UNABLE_TO_READ_THE_FILE", HttpStatus.BAD_REQUEST, "Unable to read the file"),
  ERR_EXC_UTL_002("ERROR_ON_SETTING_FIELD", HttpStatus.BAD_REQUEST, "Error on setting field"),
  ERR_EXC_UTL_003("INVALID_OBJECT_FIELD", HttpStatus.BAD_REQUEST, "Invalid Object field");

  private final HttpStatus status;
  private final String message;
  private final String exceptionName;

  ExcelException(String exceptionName, HttpStatus status, String message) {
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
