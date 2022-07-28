package com.github.giovannilamarmora.utils.math;

public class MathException extends Exception {
  private Code code;

  /**
   * Constructs a new exception with {@code null} as its detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public MathException(Code code) {
    this.code = code;
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *     {@link #getMessage()} method.
   */
  public MathException(Code code, String message) {
    super(message);
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public static enum Code {
    VALUE_NOT_PERMITTED
  }
}
