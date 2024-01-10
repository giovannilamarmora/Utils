package io.github.giovannilamarmora.utils.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorInfo {
  private String errorCode;
  private String exception;
  private HttpStatus status;
  private String message;
  private String exceptionMessage;
  private String stackTrace;
}
