package org.labs;

import java.util.concurrent.ExecutionException;

public class Main {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    AppConfig config = AppConfig.fromSystemProperties();

    int[] results = App.run(config);

    for (int foodEaten : results) {
      System.out.printf("%d ", foodEaten);
    }
    System.out.println();
  }
}
