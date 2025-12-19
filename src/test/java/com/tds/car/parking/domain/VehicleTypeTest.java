package com.tds.car.parking.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VehicleTypeTest {

    @ParameterizedTest(name = "Number {0} is mapped to {1}")
    @CsvSource({
            "1, SMALL",
            "2, MEDIUM",
            "3, LARGE",
    })
    void type_is_mapped_properly(int number, VehicleType type) {
        assertEquals(type, VehicleType.fromNumber(number));
    }

    @Test
    void non_mapped_numbers_throw_exception() {
        assertThrows(IllegalArgumentException.class, () -> VehicleType.fromNumber(12314));
    }
}