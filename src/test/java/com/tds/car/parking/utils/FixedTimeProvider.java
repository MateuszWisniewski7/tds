package com.tds.car.parking.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class FixedTimeProvider implements TimeProvider {

    private LocalDateTime current;

    public FixedTimeProvider(LocalDateTime current) {
        this.current = current;
    }

    @Override
    public LocalDateTime now() {
        return current;
    }

    public void delay(Duration duration) {
        current = current.plus(duration);
    }
}
