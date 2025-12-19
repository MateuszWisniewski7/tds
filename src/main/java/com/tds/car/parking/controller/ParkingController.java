package com.tds.car.parking.controller;

import com.tds.car.parking.domain.*;
import com.tds.car.parking.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @GetMapping
    public SpaceAllocation getSpaceAllocation() {
        return parkingService.getAllocation();
    }

    @PostMapping
    public ParkedVehicle parkVehicle(@RequestBody Vehicle vehicle) {
        return parkingService.park(vehicle);
    }

    @PostMapping("/bill")
    public ParkingBill leaveParking(@RequestBody ParkingBillRequest request) {
        return parkingService.leave(request.vehicleReg());
    }
}
