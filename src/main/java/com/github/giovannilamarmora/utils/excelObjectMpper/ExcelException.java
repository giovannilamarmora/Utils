package com.github.giovannilamarmora.utils.excelObjectMpper;

public class ExcelException extends Exception {
  private final Code code;
  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public ExcelException(Code code) {
    this.code = code;
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public ExcelException(Code code, String message) {
    super(message);
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public enum Code {
    UNABLE_TO_READ_THE_FILE,
    ERROR_ON_SETTING_FIELD,
    INVALID_OBJECT_FIELD
  }
}
