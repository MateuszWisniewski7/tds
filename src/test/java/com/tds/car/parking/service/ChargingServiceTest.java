package com.tds.car.parking.service;

import com.tds.car.parking.domain.ParkedVehicle;
import com.tds.car.parking.domain.Vehicle;
import com.tds.car.parking.domain.VehicleType;
import com.tds.car.parking.repository.BillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChargingServiceTest {

    private static final String ID = "id";
    private static final String VEHICLE_REG = "1234";
    private static final int SPACE_NUMBER = 1;
    private static final LocalDateTime NOW = LocalDateTime.now();
    private ChargingService service;

    @BeforeEach
    void setUp() {
        service = new ChargingService(() -> NOW, () -> ID, new BillRepository());
    }

    @Test
    void service_returns_proper_bill_details() {
        var timeIn = NOW.minus(Duration.ofMinutes(2));
        var parkedVehicle = new ParkedVehicle(
                new Vehicle(VEHICLE_REG, VehicleType.SMALL),
                SPACE_NUMBER,
                timeIn
        );

        var bill = service.charge(parkedVehicle);

        assertEquals(ID, bill.billId());
        assertEquals(VEHICLE_REG, bill.vehicleReg());
        assertEquals(timeIn, bill.timeIn());
        assertEquals(NOW, bill.timeOut());

    }

    @ParameterizedTest(name = "{0} vehicle should be charged {1} for {2} minutes of parking")
    @MethodSource("chargeDetails")
    void service_charges_vehicle_properly(VehicleType type, int minutes, String expectedCharge) {
        var duration = Duration.ofMinutes(minutes);
        var parkedVehicle = new ParkedVehicle(
                new Vehicle(VEHICLE_REG, type),
                SPACE_NUMBER,
                NOW.minus(duration)
        );

        var bill = service.charge(parkedVehicle);

        assertEquals(new BigDecimal(expectedCharge), bill.vehicleCharge());
    }

    static Stream<Arguments> chargeDetails() {
        return Stream.of(
                Arguments.of(VehicleType.SMALL, 1, "0.10"),
                Arguments.of(VehicleType.SMALL, 6, "1.60"),
                Arguments.of(VehicleType.SMALL, 12, "3.20"),
                Arguments.of(VehicleType.MEDIUM, 1, "0.20"),
                Arguments.of(VehicleType.MEDIUM, 6, "2.20"),
                Arguments.of(VehicleType.MEDIUM, 12, "4.40"),
                Arguments.of(VehicleType.LARGE, 1, "0.40"),
                Arguments.of(VehicleType.LARGE, 6, "3.40"),
                Arguments.of(VehicleType.LARGE, 12, "6.80")
        );
    }
}