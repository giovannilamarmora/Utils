package com.github.giovannilamarmora.utils.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

  /**
   * Return the message of the Exception
   * @return String
   */
  String getMessage();

  /**
   * Return the HttpStatus of the Exception
   * @return HttpStatus
   */
  HttpStatus getStatus();

  /**
   * return the name of the Exception
   * @return String
   */
  String name();
}
