package com.github.giovannilamarmora.utils.math;

import com.github.giovannilamarmora.utils.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MathExceptionMapper {
  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

  @ExceptionHandler(value = MathException.class)
  public ResponseEntity<ErrorResponse> exception(MathException e) {
    ErrorResponse error =
        new ErrorResponse(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.name());

    switch (e.getCode()) {
      case VALUE_NOT_PERMITTED:
        error.setError(MathException.Code.VALUE_NOT_PERMITTED.name());
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      default:
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
