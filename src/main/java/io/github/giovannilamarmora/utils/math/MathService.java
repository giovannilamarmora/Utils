package io.github.giovannilamarmora.utils.math;

import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Giovanni Lamarmora
 */
@Service
@Logged
public class MathService {

  private static final Logger LOG = LoggerFactory.getLogger(MathService.class);

  /**
   * This method is used to drop decimal values. Value double is passed with the number of decimal
   * as places
   *
   * @param value Value to be rounded
   * @param places Number of Decimal user want
   * @return Cut Number
   */
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public static double round(double value, int places) throws UtilsException {
    if (places < 0)
      throw new MathException("The current places: " + places + " is not permitted by the system.");
    BigDecimal bd = new BigDecimal(Double.toString(value));
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    LOG.info("The new number is {}", bd.doubleValue());
    return bd.doubleValue();
  }
}
