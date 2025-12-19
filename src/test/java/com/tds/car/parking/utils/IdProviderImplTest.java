package com.tds.car.parking.utils;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IdProviderImplTest {

    private final IdProviderImpl provider = new IdProviderImpl();

    @Test
    void service_provides_random_uuid_string() {
        var id = provider.provide();

        assertDoesNotThrow(() -> UUID.fromString(id));
    }
}