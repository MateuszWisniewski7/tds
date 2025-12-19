package com.tds.car.parking.service;

import com.tds.car.parking.domain.ParkedVehicle;
import com.tds.car.parking.domain.ParkingBill;
import com.tds.car.parking.domain.VehicleType;
import com.tds.car.parking.repository.BillRepository;
import com.tds.car.parking.utils.IdProvider;
import com.tds.car.parking.utils.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

import static com.tds.car.parking.domain.VehicleType.*;

@Service
@RequiredArgsConstructor
public class ChargingService {

    private static final BigDecimal ADDITIONAL_CHARGE = BigDecimal.ONE;
    private final Map<VehicleType, BigDecimal> chargesPerMinute = Map.of(
            SMALL, new BigDecimal("0.10"),
            MEDIUM, new BigDecimal("0.20"),
            LARGE, new BigDecimal("0.40")
    );
    private final TimeProvider timeProvider;
    private final IdProvider idProvider;
    private final BillRepository billRepository;

    public ParkingBill charge(ParkedVehicle parkedVehicle) {
        var timeOut = timeProvider.now();
        var duration = Duration.between(parkedVehicle.timeIn(), timeOut);
        var minutes = duration.toMinutes();
        var additionalChargesCounter = minutes / 5;
        var chargePerMinute = chargesPerMinute.get(parkedVehicle.vehicle().vehicleType());
        var standardCharge = chargePerMinute.multiply(new BigDecimal(minutes));
        var additionalCharge = ADDITIONAL_CHARGE.multiply(new BigDecimal(additionalChargesCounter));
        var fullCharge = standardCharge.add(additionalCharge);
        var bill = new ParkingBill(
                idProvider.provide(),
                parkedVehicle.vehicle().vehicleReg(),
                fullCharge,
                parkedVehicle.timeIn(),
                timeOut
        );
        return billRepository.save(bill);
    }
}
