package com.tds.car.parking.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VehicleType {
    SMALL,
    MEDIUM,
    LARGE;

    @JsonCreator
    public static VehicleType fromNumber(int typeNumber) {
        return switch (typeNumber) {
            case 1 -> SMALL;
            case 2 -> MEDIUM;
            case 3 -> LARGE;
            default -> throw new IllegalArgumentException("Unknown type: " + typeNumber);
        };
    }
}
