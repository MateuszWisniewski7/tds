package com.tds.car.parking.service;

import com.tds.car.parking.domain.Vehicle;
import com.tds.car.parking.domain.VehicleType;
import com.tds.car.parking.repository.BillRepository;
import com.tds.car.parking.utils.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final int SPACES_NUMBER = 10;
    private ParkingService service;

    @BeforeEach
    void setUp() {
        TimeProvider timeProvider = () -> NOW;
        var chargingService = new ChargingService(timeProvider, () -> "id", new BillRepository());
        service = new ParkingService(SPACES_NUMBER, timeProvider, chargingService);
    }

    @Test
    void service_returns_parking_allocation_for_empty_parking() {
        var allocation = service.getAllocation();

        assertEquals(10, allocation.availableSpaces());
        assertEquals(0, allocation.occupiedSpaces());
    }

    @Test
    void service_returns_parked_car_details() {
        var vehicle = new Vehicle("1234", VehicleType.MEDIUM);
        var parkedCar = service.park(vehicle);

        assertEquals(vehicle, parkedCar.vehicle());
        assertEquals(1, parkedCar.spaceNumber());
        assertEquals(NOW, parkedCar.timeIn());
    }

    @Test
    void service_returns_proper_allocation_after_many_cars_parked() {
        var vehicle = new Vehicle("1", VehicleType.MEDIUM);
        var vehicle2 = new Vehicle("12", VehicleType.MEDIUM);
        var vehicle3 = new Vehicle("123", VehicleType.MEDIUM);

        var parkedCar = service.park(vehicle);
        var parkedCar2 = service.park(vehicle2);
        var parkedCar3 = service.park(vehicle3);
        var allocation = service.getAllocation();

        assertEquals(7, allocation.availableSpaces());
        assertEquals(3, allocation.occupiedSpaces());
        assertEquals(1, parkedCar.spaceNumber());
        assertEquals(2, parkedCar2.spaceNumber());
        assertEquals(3, parkedCar3.spaceNumber());
    }

    @Test
    void service_parks_car_at_first_possible_space_after_one_leaves() {
        var vehicle = new Vehicle("1", VehicleType.MEDIUM);
        var vehicle2 = new Vehicle("12", VehicleType.MEDIUM);
        var vehicle3 = new Vehicle("123", VehicleType.MEDIUM);
        var vehicle4 = new Vehicle("1234", VehicleType.MEDIUM);
        var vehicle5 = new Vehicle("12345", VehicleType.MEDIUM);

        var parkedCar = service.park(vehicle);
        var parkedCar2 = service.park(vehicle2);
        var parkedCar3 = service.park(vehicle3);
        service.leave(vehicle.vehicleReg());
        var parkedCar4 = service.park(vehicle4);
        var parkedCar5 = service.park(vehicle5);
        var allocation = service.getAllocation();

        assertEquals(6, allocation.availableSpaces());
        assertEquals(4, allocation.occupiedSpaces());
        assertEquals(1, parkedCar.spaceNumber());
        assertEquals(2, parkedCar2.spaceNumber());
        assertEquals(3, parkedCar3.spaceNumber());
        assertEquals(1, parkedCar4.spaceNumber());
        assertEquals(4, parkedCar5.spaceNumber());
    }

    @Test
    void service_clears_spot_of_leaving_car() {
        var vehicle = new Vehicle("1234", VehicleType.MEDIUM);
        service.park(vehicle);

        var bill = service.leave(vehicle.vehicleReg());
        var allocation = service.getAllocation();

        assertEquals("1234", bill.vehicleReg());
        assertEquals(10, allocation.availableSpaces());
        assertEquals(0, allocation.occupiedSpaces());
    }

    @Test
    void service_throws_exception_on_same_vehicle_reg_parking() {
        var vehicle = new Vehicle("1234", VehicleType.MEDIUM);
        service.park(vehicle);

        assertThrows(IllegalStateException.class, () -> service.park(vehicle));
    }

    @Test
    void service_throws_exception_on_attempt_to_park_on_full_parking() {
        for (int i = 0; i < SPACES_NUMBER; i++) {
            service.park(new Vehicle("1234" + i, VehicleType.MEDIUM));
        }
        var vehicle = new Vehicle("1234a", VehicleType.MEDIUM);

        var exception = assertThrows(IllegalStateException.class, () -> service.park(vehicle));
        assertEquals("Parking is full", exception.getMessage());
    }

    @Test
    void service_throws_exception_on_leave_for_not_parked_vehicle() {
        assertThrows(NoSuchElementException.class, () -> service.leave("1234"));
    }
}