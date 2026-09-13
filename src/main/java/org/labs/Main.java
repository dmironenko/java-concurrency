package org.labs;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        AppConfig config = AppConfig.fromSystemProperties();

        int[] results = App.run(config);

        for (int foodEaten : results) {
            System.out.printf("%d ", foodEaten);
        }
        System.out.println();
    }
}
