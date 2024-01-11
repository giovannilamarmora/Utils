package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.exception.GenericException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.math.MathService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Logged
public class ThreadManager {

  private static final Logger LOG = LoggerFactory.getLogger(MathService.class);

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  private static void threadSeep(Integer millisecond) {
    LOG.debug("Starting process for {}", millisecond);
    try {
      LOG.info("Thread is sleeping for {} millisecond", millisecond);
      Thread.sleep(millisecond);
    } catch (InterruptedException e) {
      LOG.error("An error occurred during sleeping thread, message is {}", e.getMessage());
      throw new WebException(
          GenericException.ERR_EXC_WEB_001,
          "An error occurred during sleeping thread, message is " + e.getMessage());
    }
  }
}
