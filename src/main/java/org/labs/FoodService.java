package org.labs;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class FoodService implements AutoCloseable {
  private final Storage storage;
  private final ExecutorService executor;
  private final AtomicIntegerArray quotas;
  private final int maxServingDelayMs;

  public FoodService(Storage storage, int clientsCount, int workersCount, int maxServingDelayMs) {
    this.storage = storage;
    this.maxServingDelayMs = maxServingDelayMs;
    this.executor = Executors.newFixedThreadPool(workersCount);

    this.quotas = new AtomicIntegerArray(clientsCount);
    int totalUnits = this.storage.getUnits();
    for (int i = 0; i < clientsCount; i++) {
      int quota = totalUnits / clientsCount;
      if (i < totalUnits % clientsCount) {
        quota++;
      }

      this.quotas.set(i, quota);
    }
  }

  public Future<Boolean> tryTakeFood(int clientId, int units) {
    return this.executor.submit(
        () -> {
          int previousQuota =
              this.quotas.getAndUpdate(
                  clientId, remains -> remains >= units ? remains - units : remains);
          if (previousQuota < units) {
            return false;
          }

          if (!this.storage.tryTake(units)) {
            this.quotas.addAndGet(clientId, units);
            return false;
          }

          if (this.maxServingDelayMs > 0) {
            int delayMs = ThreadLocalRandom.current().nextInt(this.maxServingDelayMs) + 1;
            Thread.sleep(Duration.ofMillis(delayMs));
          }

          return true;
        });
  }

  @Override
  public void close() {
    this.executor.close();
  }
}
