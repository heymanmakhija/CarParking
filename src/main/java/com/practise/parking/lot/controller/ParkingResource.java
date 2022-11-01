package com.practise.parking.lot.controller;

import com.practise.parking.lot.model.Vehicle;
import com.practise.parking.lot.model.enums.VehicleType;
import com.practise.parking.lot.model.request.ParkingLotRequest;
import com.practise.parking.lot.service.ParkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/park")
@Slf4j
class ParkingResource {

    @Autowired
    ParkingService parkingService;

    @PostMapping("/addVehicle")
    ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle) {
        log.info("Adding Vehicle");
        Long id = parkingService.addVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @PostMapping("/addParkingLot")
    ResponseEntity<?> addParkingLot(@RequestBody ParkingLotRequest parkingLotRequest) {
        parkingService.addParkingLot(parkingLotRequest);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @PostMapping("/parkVehicle/{id}")
    ResponseEntity<?> parkVehicle(@PathVariable("id") Long vehicleId) {
        parkingService.parkVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @PostMapping("/unParkVehicle/{id}")
    ResponseEntity<?> unParkVehicle(@PathVariable("id") Long vehicleId) {
        Float charge = parkingService.unParkVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).body(charge);
    }


}
