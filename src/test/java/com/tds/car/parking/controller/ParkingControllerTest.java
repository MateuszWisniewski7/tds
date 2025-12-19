package com.tds.car.parking.controller;

import com.tds.car.parking.domain.ParkedVehicle;
import com.tds.car.parking.domain.ParkingBill;
import com.tds.car.parking.domain.SpaceAllocation;
import com.tds.car.parking.domain.Vehicle;
import com.tds.car.parking.service.ParkingService;
import com.tds.car.parking.utils.FixedTimeProvider;
import com.tds.car.parking.utils.IdProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.tds.car.parking.domain.VehicleType.SMALL;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureRestTestClient
class ParkingControllerTest {

    private static final LocalDateTime NOW = LocalDateTime.parse("2025-12-06T12:24:11");
    private static final String BILL_ID = "id";

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient client;
    @Autowired
    private ParkingService parkingService;
    @Autowired
    private FixedTimeProvider fixedTimeProvider;

    @Test
    void controller_responds_with_available_and_occupied_spaces() {
        client.get()
                .uri("http://localhost:%d/parking".formatted(port))
                .exchange()
                .expectBody(SpaceAllocation.class)
                .isEqualTo(new SpaceAllocation(100, 0));
    }

    @Test
    void controller_parks_given_vehicle_and_returns_its_registration_space_number_and_time_in() {
        var vehicle = new Vehicle("123", SMALL);
        var vehicleReg2 = "1234";
        parkingService.park(vehicle);

        client.post()
                .uri("http://localhost:%d/parking".formatted(port))
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "vehicleReg": "1234",
                          "vehicleType": 2
                        }""")
                .exchange()
                .expectBody(ParkedVehicle.class)
                .isEqualTo(new ParkedVehicle(new Vehicle(vehicleReg2, null), 2, NOW));
        parkingService.leave(vehicle.vehicleReg());
        parkingService.leave(vehicleReg2);
    }

    @Test
    void controller_returns_error_on_attempt_of_parking_same_car() {
        var vehicle = new Vehicle("123", SMALL);
        parkingService.park(vehicle);

        client.post()
                .uri("http://localhost:%d/parking".formatted(port))
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "vehicleReg": "123",
                          "vehicleType": 1
                        }""")
                .exchange()
                .expectBody()
                .json("""
                        {
                        "detail":"Car with registration[123] was already parked",
                        "instance":"/parking",
                        "status":400,
                        "title":"Bad Request"
                        }
                        """);
        parkingService.leave(vehicle.vehicleReg());
    }

    @Test
    void controller_returns_error_on_attempt_of_leaving_non_parked_car() {
        client.post()
                .uri("http://localhost:%d/parking/bill".formatted(port))
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "vehicleReg": "123"
                        }""")
                .exchange()
                .expectBody()
                .json("""
                        {
                        "detail":"Vehicle with registration[123] was not found",
                        "instance":"/parking/bill",
                        "status":404,
                        "title":"Not Found"
                        }
                        """);
    }

    @Test
    void controller_frees_up_space_and_returns_final_charge_and_parking_time() {
        parkingService.park(new Vehicle("123", SMALL));
        fixedTimeProvider.delay(Duration.ofHours(1));

        client.post()
                .uri("http://localhost:%d/parking/bill".formatted(port))
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "vehicleReg": "123"
                        }""")
                .exchange()
                .expectBody(ParkingBill.class)
                .isEqualTo(new ParkingBill(
                        BILL_ID,
                        "123",
                        new BigDecimal("18.00"),
                        NOW,
                        LocalDateTime.parse("2025-12-06T13:24:11")));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        FixedTimeProvider fixedTimeProvider() {
            return new FixedTimeProvider(NOW);
        }

        @Bean
        @Primary
        IdProvider idProvider() {
            return () -> BILL_ID;
        }
    }
}
