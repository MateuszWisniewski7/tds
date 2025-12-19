package com.tds.car.parking.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDateTime;

public record ParkedVehicle(@JsonUnwrapped Vehicle vehicle,
                            Integer spaceNumber,
                            LocalDateTime timeIn
) {
}
