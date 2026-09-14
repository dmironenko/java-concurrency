package org.labs;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class FoodService implements AutoCloseable {
  private final Storage storage;
  private final ExecutorService executor;
  private final PriorityBlockingQueue<FoodRequest> foodRequests;
  private final AtomicIntegerArray servedFood;
  private final int maxServingDelayMs;

  public FoodService(Storage storage, int clientsCount, int workersCount, int maxServingDelayMs) {
    this.storage = storage;
    this.servedFood = new AtomicIntegerArray(clientsCount);
    this.maxServingDelayMs = maxServingDelayMs;
    this.foodRequests = new PriorityBlockingQueue<>();

    this.executor = Executors.newFixedThreadPool(workersCount);
    for (int i = 0; i < workersCount; i++) {
      this.executor.submit(this::handleFoodRequests);
    }
  }

  public Future<Boolean> tryTakeFood(int clientId, int units) {
    FutureTask<Boolean> task =
        new FutureTask<>(
            () -> {
              if (!this.storage.tryTake(units)) {
                return false;
              }

              this.servedFood.addAndGet(clientId, units);

              if (this.maxServingDelayMs > 0) {
                int delayMs = ThreadLocalRandom.current().nextInt(this.maxServingDelayMs) + 1;
                try {
                  Thread.sleep(Duration.ofMillis(delayMs));
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  throw e;
                }
              }

              return true;
            });

    this.foodRequests.add(new FoodRequest(this.servedFood.get(clientId), task));

    return task;
  }

  private void handleFoodRequests() {
    try {
      while (true) {
        FoodRequest request = this.foodRequests.take();
        request.task().run();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void close() {
    this.executor.shutdownNow();
    this.executor.close();
  }

  private record FoodRequest(int servedFood, FutureTask<Boolean> task)
      implements Comparable<FoodRequest> {
    @Override
    public int compareTo(FoodRequest other) {
      return Integer.compare(this.servedFood, other.servedFood);
    }
  }
}
