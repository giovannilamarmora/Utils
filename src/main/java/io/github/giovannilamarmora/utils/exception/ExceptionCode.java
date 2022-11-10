package io.github.giovannilamarmora.utils.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

  /**
   * return the name of the Exception
   *
   * @return String
   */
  String exceptionName();

  /**
   * Return the message of the Exception
   *
   * @return String
   */
  String getMessage();

  /**
   * Return the HttpStatus of the Exception
   *
   * @return HttpStatus
   */
  HttpStatus getStatus();

  /**
   * return the code of the Exception
   *
   * @return String
   */
  String name();
}
