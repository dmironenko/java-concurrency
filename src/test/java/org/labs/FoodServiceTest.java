package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class FoodServiceTest {
  @Test
  void concurrentTakeFood() throws Exception {
    Storage storage = new Storage(10);

    try (FoodService service = new FoodService(storage, 2, 2, 0)) {
      var results = new ArrayList<Future<Boolean>>();
      for (int i = 0; i < 20; i++) {
        results.add(service.tryTakeFood(0, 1));
      }

      int accepted = 0;
      for (Future<Boolean> response : results) {
        if (response.get()) {
          accepted++;
        }
      }

      assertEquals(5, accepted);
      assertEquals(5, storage.getUnits());
      assertTrue(service.tryTakeFood(1, 5).get());
      assertEquals(0, storage.getUnits());
    }
  }
}
