package org.labs;

public class App {
    public static int[] run(AppConfig config) throws InterruptedException {
        int programmersCount = config.programmersCount();
        Storage storage = new Storage(config.storageUnits());

        try (FoodService foodService = new FoodService(storage, programmersCount, config.waitersCount())) {
            Spoon[] spoons = new Spoon[programmersCount];
            for (int i = 0; i < programmersCount; i++) {
                spoons[i] = new Spoon(i);
            }

            Programmer[] programmers = new Programmer[programmersCount];
            for (int i = 0; i < programmersCount; i++) {
                programmers[i] = new Programmer(
                    i, foodService, spoons[i], spoons[(i + 1) % programmersCount]
                );
            }

            Thread[] threads = new Thread[programmersCount];
            for (int i = 0; i < programmersCount; i++) {
                threads[i] = new Thread(programmers[i]);
            }

            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            int[] results = new int[programmersCount];
            for (int i = 0; i < programmersCount; i++) {
                results[i] = programmers[i].getFoodEaten();
            }

            return results;
        }
    }
}
