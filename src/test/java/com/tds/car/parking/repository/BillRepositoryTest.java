package com.tds.car.parking.repository;

import com.tds.car.parking.domain.ParkingBill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BillRepositoryTest {

    private BillRepository repository;

    @BeforeEach
    void setUp() {
        repository = new BillRepository();
    }

    @Test
    void repository_saves_bill() {
        var now = LocalDateTime.now();
        var bill = new ParkingBill("id", "123", BigDecimal.ONE, now, now.plusMinutes(2));

        assertEquals(bill, repository.save(bill));
    }

    @Test
    void repository_returns_saved_bill() {
        var now = LocalDateTime.now();
        var bill = new ParkingBill("id", "123", BigDecimal.ONE, now, now.plusMinutes(2));

        repository.save(bill);

        assertEquals(bill, repository.get(bill.billId()).orElseThrow());
    }
}