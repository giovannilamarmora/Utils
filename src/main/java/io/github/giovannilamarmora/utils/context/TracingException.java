package io.github.giovannilamarmora.utils.context;

import io.github.giovannilamarmora.utils.exception.ExceptionCode;
import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.exception.UtilsException;

public class TracingException extends UtilsException {

  private static final ExceptionCode DEFAULT_CODE = GenericException.ERR_CONF_PRO_001;

  public TracingException(String message) {
    super(DEFAULT_CODE, message);
  }
}
