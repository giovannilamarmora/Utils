package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Logged
public class ThreadManager {

  private static final Logger LOG = LoggerFilter.getLogger(ThreadManager.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static void threadSeep(Integer millisecond) {
    LOG.debug("Starting process for {}", millisecond);
    try {
      LOG.info("Thread is sleeping for {} millisecond", millisecond);
      Thread.sleep(millisecond);
    } catch (InterruptedException e) {
      LOG.error("An error occurred during sleeping thread, message is {}", e.getMessage());
      throw new WebException(
          GenericException.ERR_EXC_WEB_002,
          "An error occurred during sleeping thread, message is " + e.getMessage());
    }
  }
}
