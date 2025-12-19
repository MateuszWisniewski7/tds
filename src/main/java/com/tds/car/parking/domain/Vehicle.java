package com.tds.car.parking.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Vehicle(String vehicleReg,
                      @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
                      VehicleType vehicleType
) {
}
