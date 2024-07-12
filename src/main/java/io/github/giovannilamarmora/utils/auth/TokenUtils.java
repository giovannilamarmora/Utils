package io.github.giovannilamarmora.utils.auth;

import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Logged
public interface TokenUtils {

  Logger LOG = LoggerFilter.getLogger(TokenUtils.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  static String hashingToken(String token) {
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      LOG.error(
          "An error happen during the generation of the message digest, message is {}",
          e.getMessage());
      throw new UtilsException(GenericException.ERR_DEF_UTL_001, e.getMessage());
    }
    messageDigest.update(token.getBytes());
    return Base64.encodeBase64URLSafeString(messageDigest.digest());
  }
}
