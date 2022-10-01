package com.github.giovannilamarmora.utils.excelObjectMpper;

import com.github.giovannilamarmora.utils.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExcelExceptionMapper {
  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

  @ExceptionHandler(value = ExcelException.class)
  public ResponseEntity<ErrorResponse> exception(ExcelException e) {
    ErrorResponse error =
        new ErrorResponse(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.name());

    switch (e.getCode()) {
      case UNABLE_TO_READ_THE_FILE:
        error.setError(ExcelException.Code.UNABLE_TO_READ_THE_FILE.name());
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      case ERROR_ON_SETTING_FIELD:
        error.setError(ExcelException.Code.ERROR_ON_SETTING_FIELD.name());
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      case INVALID_OBJECT_FIELD:
        error.setError(ExcelException.Code.INVALID_OBJECT_FIELD.name());
        error.setStatus(HttpStatus.BAD_REQUEST.name());
        error.setMessage(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
      default:
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
