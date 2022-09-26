package com.github.giovannilamarmora.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
  public static double round(double value, int places) throws MathException {
    if (places < 0)
      throw new MathException(
          MathException.Code.VALUE_NOT_PERMITTED,
          "The current places: " + places + " is not permitted by the system.");
    BigDecimal bd = new BigDecimal(Double.toString(value));
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    LOG.info("The new number is {}", bd.doubleValue());
    return bd.doubleValue();
  }
}