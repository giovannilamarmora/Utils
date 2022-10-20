package com.github.giovannilamarmora.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.giovannilamarmora.utils.exception.UtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Giovanni Lamarmora
 */
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
  public static double round(double value, int places) throws UtilsException {
    if (places < 0)
      throw new UtilsException(
          MathException.ERRMATUTL001,
          "The current places: " + places + " is not permitted by the system.");
    BigDecimal bd = new BigDecimal(Double.toString(value));
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    LOG.info("The new number is {}", bd.doubleValue());
    return bd.doubleValue();
  }
}
