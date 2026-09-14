package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class FoodServiceTest {
  @Test
  void concurrentTakeFood() throws Exception {
    Storage storage = new Storage(10);

    try (FoodService service = new FoodService(storage, 20, 2, 0)) {
      var results = new ArrayList<Future<Boolean>>();
      for (int i = 0; i < 20; i++) {
        results.add(service.tryTakeFood(i, 1));
      }

      int accepted = 0;
      for (Future<Boolean> response : results) {
        if (response.get()) {
          accepted++;
        }
      }

      assertEquals(10, accepted);
      assertEquals(0, storage.getUnits());
      assertFalse(service.tryTakeFood(0, 1).get());
    }
  }
}
