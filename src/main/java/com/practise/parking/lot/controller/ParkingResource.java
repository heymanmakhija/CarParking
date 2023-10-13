package com.practise.parking.lot.controller;

import com.practise.parking.lot.model.Vehicle;
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

    // Multilevel Parking Place
    // -> Parking Lots located at different floor.
    // -> Different no. of available Parking Spot for unique vehicle.
    // Vehicle comes from any entry gate, gets a ticket and leaves from an exit gate, gets a bill.

    // post api to create a multilevel parking place with name, address.

    // Associate parking lot with multilevel parking place.
    // Take note of floor as well, no. of parking spots available for each vehicle type.
    @PostMapping("/addParkingLot")
    ResponseEntity<?> addParkingLot(@RequestBody ParkingLotRequest parkingLotRequest) {
        parkingService.addParkingLot(parkingLotRequest);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    // Take note of entry gate as well, Return Ticket.
    @PostMapping("/addVehicle")
    ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle) {
        log.info("Adding Vehicle");
        Long id = parkingService.addVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    // Take note of parking spot as well.
    @PostMapping("/parkVehicle/{id}")
    ResponseEntity<?> parkVehicle(@PathVariable("id") Long vehicleId) {
        parkingService.parkVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    // Take note of exit gate as well. Return bill.
    @PostMapping("/unParkVehicle/{id}")
    ResponseEntity<?> unParkVehicle(@PathVariable("id") Long vehicleId) {
        Float charge = parkingService.unParkVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).body(charge);
    }


}
