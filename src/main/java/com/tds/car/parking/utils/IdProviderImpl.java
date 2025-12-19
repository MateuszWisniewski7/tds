package com.tds.car.parking.utils;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdProviderImpl implements IdProvider {

    @Override
    public String provide() {
        return UUID.randomUUID().toString();
    }
}
