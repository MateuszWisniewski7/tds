package com.tds.car.parking.repository;

import com.tds.car.parking.domain.ParkingBill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class BillRepository {
    private final Map<String, ParkingBill> bills = new ConcurrentHashMap<>();

    public ParkingBill save(ParkingBill bill) {
        var id = bill.billId();
        log.info("Saving bill with id[{}]", id);
        bills.put(id, bill);
        return bill;
    }

    public Optional<ParkingBill> get(String id) {
        log.info("Querying bill with id[{}]", id);
        return Optional.ofNullable(bills.get(id));
    }
}
