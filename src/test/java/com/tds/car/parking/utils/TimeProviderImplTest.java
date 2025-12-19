package com.tds.car.parking.utils;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeProviderImplTest {

    private final TimeProviderImpl provider = new TimeProviderImpl();

    @Test
    void provider_returns_local_date_time_in_utc() {
        var nowInPlusHourZone = LocalDateTime.now(ZoneOffset.ofHours(1));

        var now = provider.now();

        assertEquals(1, Duration.between(now, nowInPlusHourZone).toHours());
    }
}