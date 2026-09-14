package org.labs;

public record AppConfig(
    int programmersCount,
    int storageUnits,
    int waitersCount,
    int maxServingDelayMs,
    int maxEatingDelayMs) {
  public static AppConfig fromSystemProperties() {
    return new AppConfig(
        Integer.getInteger("programmers", 7),
        Integer.getInteger("food", 1_000_000),
        Integer.getInteger("waiters", 2),
        Integer.getInteger("maxServingDelayMs", 5),
        Integer.getInteger("maxEatingDelayMs", 10));
  }
}
