package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class ProgrammerTest {
  @Test
  void eatFoodAndReleaseSpoons() throws Exception {
    Storage storage = new Storage(10);
    Spoon first = new Spoon(0);
    Spoon second = new Spoon(1);

    try (FoodService service = new FoodService(storage, 3, 2, 0);
        var executor = Executors.newSingleThreadExecutor()) {
      Programmer programmer = new Programmer(0, service, second, first, 0);

      executor.submit(programmer).get();
      assertEquals(0, storage.getUnits());

      first.take();

      try {
        second.take();

        try {
          assertEquals(10, programmer.getFoodEaten());
        } finally {
          second.put();
        }
      } finally {
        first.put();
      }
    }
  }
}
