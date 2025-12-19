package com.tds.car.parking.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParkingBill(
        String billId,
        String vehicleReg,
        BigDecimal vehicleCharge,
        LocalDateTime timeIn,
        LocalDateTime timeOut
) {
}
