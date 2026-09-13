package org.labs;

import java.util.concurrent.locks.ReentrantLock;

public class Spoon {
    private final int id;
    private final ReentrantLock lock = new ReentrantLock();

    public Spoon(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void take() {
        lock.lock();
    }

    public void put() {
        lock.unlock();
    }
}
