package com.github.giovannilamarmora.utils.dto;

import java.io.Serializable;

public class ErrorResponse implements Serializable {

  private String error;
  private String status;
  private String message;

  public ErrorResponse(String error, String status) {
    this.error = error;
    this.status = status;
  }

  public ErrorResponse(String error, String status, String message) {
    this.error = error;
    this.status = status;
    this.message = message;
  }

  public ErrorResponse() {}

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
