package org.labs;

import java.util.concurrent.atomic.AtomicInteger;

public class Storage {
    private final AtomicInteger units;

    public Storage(int units) {
        this.units = new AtomicInteger(units);
    }

    public int getUnits() {
        return this.units.get();
    }

    public boolean tryTake(int units) {
        return this.units.getAndUpdate(current -> current >= units ? current - units : current) >= units;
    }
}
