package org.labs;

public record AppConfig(int programmersCount, int storageUnits, int waitersCount) {
    public static AppConfig fromSystemProperties() {
        return new AppConfig(
            Integer.getInteger("programmers", 7),
            Integer.getInteger("food", 1_000_000),
            Integer.getInteger("waiters", 2)
        );
    }
}
