package com.github.giovannilamarmora.utils.excelObjectMpper;

import com.github.giovannilamarmora.utils.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public enum ExcelException implements ExceptionCode {
  UNABLE_TO_READ_THE_FILE(HttpStatus.BAD_REQUEST, null),
  ERROR_ON_SETTING_FIELD(HttpStatus.BAD_REQUEST, null),
  INVALID_OBJECT_FIELD(HttpStatus.BAD_REQUEST, null);

  ExcelException(HttpStatus status, String message) {}

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
