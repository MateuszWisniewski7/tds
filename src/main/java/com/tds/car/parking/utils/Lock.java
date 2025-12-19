package com.tds.car.parking.utils;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class Lock {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public <T> T readLock(Supplier<T> supplier) {
        try {
            lock.readLock().lock();
            return supplier.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    public <T> T writeLock(Supplier<T> supplier) {
        try {
            lock.writeLock().lock();
            return supplier.get();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
