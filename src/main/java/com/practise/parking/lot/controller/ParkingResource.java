package com.practise.parking.lot.controller;

import com.practise.parking.lot.model.Vehicle;
import com.practise.parking.lot.model.request.ParkingLotRequest;
import com.practise.parking.lot.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/park")
@Slf4j
@Tag(name = "Parking", description = "Endpoints for parking lot creation, vehicle intake, parking, and checkout.")
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
    @Operation(
            summary = "Create parking lot inventory for a floor",
            description = "Adds parking spots for a given floor by vehicle spot type.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ParkingLotRequest.class),
                            examples = @ExampleObject(
                                    value = "{\"bike\":10,\"compact\":20,\"large\":5,\"floor\":\"GROUND\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parking lot created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    ResponseEntity<?> addParkingLot(@RequestBody ParkingLotRequest parkingLotRequest) {
        parkingService.addParkingLot(parkingLotRequest);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    // Take note of entry gate as well, Return Ticket.
    @PostMapping("/addVehicle")
    @Operation(
            summary = "Register a vehicle entry",
            description = "Creates a vehicle record and returns the generated ticket or vehicle id.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Vehicle.class),
                            examples = @ExampleObject(
                                    value = "{\"vehicleNo\":\"DL01AB1234\",\"type\":\"CAR\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vehicle registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid vehicle payload")
            }
    )
    ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle) {
        log.info("Adding Vehicle");
        Long id = parkingService.addVehicle(vehicle);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    // Take note of parking spot as well.
    @PostMapping("/parkVehicle/{id}")
    @Operation(
            summary = "Assign a parking spot to a vehicle",
            description = "Finds a compatible free spot and marks it occupied for the provided vehicle id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vehicle parked"),
                    @ApiResponse(responseCode = "404", description = "Vehicle not found"),
                    @ApiResponse(responseCode = "409", description = "No parking spot available")
            }
    )
    ResponseEntity<?> parkVehicle(@PathVariable("id") Long vehicleId) throws Exception {
        parkingService.parkVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    // Take note of exit gate as well. Return bill.
    @PostMapping("/unParkVehicle/{id}")
    @Operation(
            summary = "Unpark a vehicle and calculate charge",
            description = "Releases the allocated spot and returns the computed parking bill.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Vehicle unparked and charge returned"),
                    @ApiResponse(responseCode = "404", description = "Vehicle or parking spot not found")
            }
    )
    ResponseEntity<?> unParkVehicle(@PathVariable("id") Long vehicleId) throws Exception {
        Float charge = parkingService.unParkVehicle(vehicleId);
        return ResponseEntity.status(HttpStatus.OK).body(charge);
    }


}
