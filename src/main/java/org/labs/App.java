package org.labs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App {
  public static int[] run(AppConfig config) throws InterruptedException, ExecutionException {
    int programmersCount = config.programmersCount();
    Storage storage = new Storage(config.storageUnits());

    try (FoodService foodService =
            new FoodService(
                storage, programmersCount, config.waitersCount(), config.maxServingDelayMs());
        ExecutorService programmersExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
      Spoon[] spoons = new Spoon[programmersCount];
      for (int i = 0; i < programmersCount; i++) {
        spoons[i] = new Spoon(i);
      }

      Programmer[] programmers = new Programmer[programmersCount];
      for (int i = 0; i < programmersCount; i++) {
        programmers[i] =
            new Programmer(
                i,
                foodService,
                spoons[i],
                spoons[(i + 1) % programmersCount],
                config.maxEatingDelayMs());
      }

      List<Future<?>> futures = new ArrayList<>(programmersCount);
      for (Programmer programmer : programmers) {
        futures.add(programmersExecutor.submit(programmer));
      }

      for (Future<?> future : futures) {
        future.get();
      }

      int[] results = new int[programmersCount];
      for (int i = 0; i < programmersCount; i++) {
        results[i] = programmers[i].getFoodEaten();
      }

      return results;
    }
  }
}
