package org.labs;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class Programmer implements Runnable {
  private final int id;

  private final FoodService foodService;
  private final int maxEatingDelayMs;
  private int foodEaten;

  private final Spoon firstPrioritySpoon, secondPrioritySpoon;

  public Programmer(
      int id, FoodService foodService, Spoon firstSpoon, Spoon secondSpoon, int maxEatingDelayMs) {
    this.id = id;

    this.foodService = foodService;
    this.maxEatingDelayMs = maxEatingDelayMs;
    this.foodEaten = 0;

    if (firstSpoon.getId() < secondSpoon.getId()) {
      this.firstPrioritySpoon = firstSpoon;
      this.secondPrioritySpoon = secondSpoon;
    } else {
      this.firstPrioritySpoon = secondSpoon;
      this.secondPrioritySpoon = firstSpoon;
    }
  }

  public int getId() {
    return this.id;
  }

  public int getFoodEaten() {
    return this.foodEaten;
  }

  @Override
  public void run() {
    while (true) {
      Future<Boolean> foodFutureResponse = this.foodService.tryTakeFood(this.id, 1);

      boolean foodResponse;
      try {
        foodResponse = foodFutureResponse.get();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      } catch (ExecutionException e) {
        throw new IllegalStateException("failed to get food", e.getCause());
      }

      if (!foodResponse) {
        return;
      }

      this.firstPrioritySpoon.take();

      try {
        this.secondPrioritySpoon.take();

        try {
          if (this.maxEatingDelayMs > 0) {
            int delayMs = ThreadLocalRandom.current().nextInt(this.maxEatingDelayMs) + 1;
            try {
              Thread.sleep(Duration.ofMillis(delayMs));
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
          }

          this.foodEaten++;
        } finally {
          this.secondPrioritySpoon.put();
        }
      } finally {
        this.firstPrioritySpoon.put();
      }
    }
  }
}
