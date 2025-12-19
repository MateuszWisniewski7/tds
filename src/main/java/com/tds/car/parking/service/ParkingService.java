package com.tds.car.parking.service;

import com.tds.car.parking.domain.ParkedVehicle;
import com.tds.car.parking.domain.ParkingBill;
import com.tds.car.parking.domain.SpaceAllocation;
import com.tds.car.parking.domain.Vehicle;
import com.tds.car.parking.utils.Lock;
import com.tds.car.parking.utils.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ParkingService {

    private final Lock lock = new Lock();
    private final Map<String, ParkedVehicle> parkedVehicles = new HashMap<>();
    private final NavigableSet<Integer> freeSpaces;
    private final TimeProvider timeProvider;
    private final ChargingService chargingService;

    public ParkingService(
            @Value("${parking.spaces.number}") Integer spacesNumber,
            TimeProvider timeProvider, ChargingService chargingService
    ) {
        this.freeSpaces = IntStream.range(1, spacesNumber + 1).boxed().collect(Collectors.toCollection(TreeSet::new));
        this.timeProvider = timeProvider;
        this.chargingService = chargingService;
    }

    public SpaceAllocation getAllocation() {
        return lock.readLock(() -> new SpaceAllocation(freeSpaces.size(), parkedVehicles.size()));
    }

    public ParkedVehicle park(Vehicle vehicle) {
        log.info("Parking vehicle[{}]", vehicle);
        var vehicleReg = vehicle.vehicleReg();
        return lock.writeLock(() -> parkedVehicles.compute(vehicleReg, (reg, parkedVehicle) -> {
            if (parkedVehicle != null) {
                throw new IllegalStateException("Car with registration[%s] was already parked".formatted(vehicleReg));
            }
            var firstFreeSpot = freeSpaces.pollFirst();
            if (firstFreeSpot == null) {
                throw new IllegalStateException("Parking is full");
            }
            return new ParkedVehicle(vehicle, firstFreeSpot, timeProvider.now());
        }));
    }

    public ParkingBill leave(String vehicleReg) {
        return lock.writeLock(() -> {
            log.info("Vehicle with registration[{}] is leaving parking", vehicleReg);
            var parkedVehicle = parkedVehicles.remove(vehicleReg);
            if (parkedVehicle == null) {
                throw new NoSuchElementException("Vehicle with registration[%s] was not found".formatted(vehicleReg));
            }
            freeSpaces.add(parkedVehicle.spaceNumber());
            var bill = chargingService.charge(parkedVehicle);
            log.info("Vehicle was charged with bill[{}]", bill);
            return bill;
        });
    }
}
