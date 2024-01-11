package io.github.giovannilamarmora.utils.interceptors.math;

import static org.junit.jupiter.api.Assertions.*;

import io.github.giovannilamarmora.utils.exception.UtilsException;
import io.github.giovannilamarmora.utils.math.MathException;
import io.github.giovannilamarmora.utils.math.MathService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MathServiceTest {

  @Autowired private MathService mathService;

  @Test
  public void testRoundWithValidInput() throws UtilsException {
    double result = MathService.round(5.678, 2);
    assertEquals(5.68, result, 0.01);
  }

  @Test
  public void testRoundWithNegativePlacesShouldThrowException() {
    assertThrows(MathException.class, () -> MathService.round(5.678, -2));
  }
}
