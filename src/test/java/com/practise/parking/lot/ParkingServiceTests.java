package com.practise.parking.lot;

import com.practise.parking.lot.model.Vehicle;
import com.practise.parking.lot.model.enums.VehicleType;
import com.practise.parking.lot.service.ParkingService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Date;

@SpringBootTest
class ParkingServiceTests {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ParkingService parkingService;

    @Test
    void calculateBikeCharge() {
        Vehicle vehicle = new Vehicle(1L, "UP78DC7568", VehicleType.BIKE, (float) 0, new Date(), new Date(), new Date());
        Float charge = parkingService.calculateCharge(vehicle);
        logger.debug("charge -> " + charge);
        assert (charge == 0);
    }

    @Test
    void calculateBikeChargeForAnHour() {
        Date addHours = new Date();
        addHours.setHours(new Date().getHours() + 1);
        Vehicle vehicle = new Vehicle(1L, "UP78DC7568", VehicleType.BIKE, (float) 0, new Date(), addHours, new Date());
        Float charge = parkingService.calculateCharge(vehicle);
        logger.debug("charge -> " + charge);
        assert (charge == 20);
    }

    @Test
    void calculateTruckChargeFor10Hour() {
        Date addHours = new Date();
        addHours.setHours(new Date().getHours() + 10);
        Vehicle vehicle = new Vehicle(1L, "UP78DC7568", VehicleType.TRUCK, (float) 0, new Date(), addHours, new Date());
        Float charge = parkingService.calculateCharge(vehicle);
        logger.debug("charge -> " + charge);
        assert (charge == 1000);
    }

}
