package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

class StorageTest {
  @Test
  void takeFood() {
    Storage storage = new Storage(10);

    assertTrue(storage.tryTake(4));
    assertEquals(6, storage.getUnits());

    assertTrue(storage.tryTake(6));
    assertEquals(0, storage.getUnits());

    assertFalse(storage.tryTake(1));
    assertEquals(0, storage.getUnits());
  }

  @Test
  void concurrentTakeFood() throws Exception {
    Storage storage = new Storage(1);

    CountDownLatch ready = new CountDownLatch(2);
    CountDownLatch start = new CountDownLatch(1);

    try (var executor = Executors.newFixedThreadPool(2)) {
      var first =
          executor.submit(
              () -> {
                ready.countDown();
                start.await();
                return storage.tryTake(1);
              });
      var second =
          executor.submit(
              () -> {
                ready.countDown();
                start.await();
                return storage.tryTake(1);
              });

      try {
        ready.await();
      } finally {
        start.countDown();
      }

      boolean firstResult = first.get();
      boolean secondResult = second.get();
      assertTrue(firstResult != secondResult);
      assertEquals(0, storage.getUnits());
    }
  }
}
