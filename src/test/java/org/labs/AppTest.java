package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AppTest {
  @ParameterizedTest
  @CsvSource({"7, 1000000, 2", "7, 2, 2", "7, 0, 2"})
  void regular(int clients, int food, int workers) throws InterruptedException, ExecutionException {
    int[] results = App.run(new AppConfig(clients, food, workers, 0, 0));

    assertEquals(clients, results.length);
    assertEquals(food, Arrays.stream(results).sum());
    assertTrue(Arrays.stream(results).allMatch(value -> value >= 0));

    int min = Arrays.stream(results).min().orElseThrow();
    int max = Arrays.stream(results).max().orElseThrow();
    assertTrue(max - min <= 1, Arrays.toString(results));
  }

  @Test
  void regularWithDelays() throws InterruptedException, ExecutionException {
    int[] results = App.run(new AppConfig(7, 21, 2, 2, 3));

    assertEquals(21, Arrays.stream(results).sum());
    assertTrue(Arrays.stream(results).allMatch(value -> value > 0));
  }
}
