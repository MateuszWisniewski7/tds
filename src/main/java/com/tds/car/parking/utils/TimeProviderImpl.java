package com.tds.car.parking.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TimeProviderImpl implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
