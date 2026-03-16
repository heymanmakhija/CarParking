package com.practise.parking.lot.service;

import com.practise.parking.lot.model.ParkingLot;
import com.practise.parking.lot.model.ParkingSpot;
import com.practise.parking.lot.model.Vehicle;
import com.practise.parking.lot.model.enums.ParkingSpotType;
import com.practise.parking.lot.model.enums.VehicleType;
import com.practise.parking.lot.model.request.ParkingLotRequest;
import com.practise.parking.lot.model.response.ParkedVehicleSummaryItem;
import com.practise.parking.lot.model.response.ParkedVehicleSummaryResponse;
import com.practise.parking.lot.repository.ParkingLotRepository;
import com.practise.parking.lot.repository.ParkingSpotRepository;
import com.practise.parking.lot.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
@Slf4j
public class ParkingService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    ParkingLotRepository parkingLotRepository;

    @Autowired
    ParkingSpotRepository parkingSpotRepository;

    public Long addVehicle(Vehicle vehicle) {
        String vehicleNo = vehicle.getVehicleNo();
        if (!StringUtils.hasText(vehicleNo)) {
            throw new ResponseStatusException(BAD_REQUEST, "vehicleNo is required");
        }

        if (vehicle.getType() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "vehicle type is required");
        }

        String normalizedVehicleNo = vehicleNo.trim().toUpperCase();
        if (vehicleRepository.existsByVehicleNo(normalizedVehicleNo)) {
            throw new ResponseStatusException(CONFLICT, "Vehicle already exists with vehicleNo: " + normalizedVehicleNo);
        }

        if (!hasAvailableSpot(vehicle.getType())) {
            throw new ResponseStatusException(CONFLICT, "No parking spot available for vehicle type: " + vehicle.getType());
        }

        vehicle.setVehicleNo(normalizedVehicleNo);
        Vehicle newVehicle = vehicleRepository.save(vehicle);
        assignParkingSpot(newVehicle);
        return newVehicle.getId();
    }

    public void addParkingLot(ParkingLotRequest parkingLotRequest) {

        List<ParkingSpot> parkingSpotList = new ArrayList<>();

        if (parkingLotRequest.getBike() > 0) {
            int count = parkingLotRequest.getBike();
            for (int i = 0; i < count; i++) {
                ParkingSpot parkingSpot = new ParkingSpot();
                parkingSpot.setType(ParkingSpotType.BIKE);
                parkingSpotList.add(parkingSpot);
                parkingSpotRepository.save(parkingSpot);
            }
        }

        if (parkingLotRequest.getCompact() > 0) {
            int count = parkingLotRequest.getCompact();
            for (int i = 0; i < count; i++) {
                ParkingSpot parkingSpot = new ParkingSpot();
                parkingSpot.setType(ParkingSpotType.COMPACT);
                parkingSpotList.add(parkingSpot);
                parkingSpotRepository.save(parkingSpot);
            }
        }

        if (parkingLotRequest.getLarge() > 0) {
            int count = parkingLotRequest.getLarge();
            for (int i = 0; i < count; i++) {
                ParkingSpot parkingSpot = new ParkingSpot();
                parkingSpot.setType(ParkingSpotType.LARGE);
                parkingSpotList.add(parkingSpot);
                parkingSpotRepository.save(parkingSpot);
            }
        }

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setSpots(parkingSpotList);
        parkingLot.setFloor(parkingLotRequest.getFloor());
        parkingLotRepository.save(parkingLot);
    }

    public void parkVehicle(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Vehicle not found with id: " + vehicleId));
        if (parkingSpotRepository.findByVehicleId(vehicleId) != null) {
            throw new ResponseStatusException(CONFLICT, "Vehicle is already parked with id: " + vehicleId);
        }

        assignParkingSpot(vehicle);
    }

    public Float unParkVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Vehicle not found with id: " + vehicleId));
        return unParkVehicle(vehicle);
    }

    public Float unParkVehicle(String vehicleNo) {
        Vehicle vehicle = vehicleRepository.findByVehicleNo(vehicleNo);
        if (vehicle == null) {
            throw new ResponseStatusException(NOT_FOUND, "Vehicle not found with vehicleNo: " + vehicleNo);
        }
        return unParkVehicle(vehicle);
    }

    private Float unParkVehicle(Vehicle vehicle) {
        ParkingSpot parkingSpot = parkingSpotRepository.findByVehicleId(vehicle.getId());
        if (parkingSpot == null) {
            throw new ResponseStatusException(NOT_FOUND, "Parking spot not found for vehicle: " + vehicle.getId());
        }

        parkingSpot.setFree(true);
        parkingSpot.setVehicle(null);
        parkingSpotRepository.save(parkingSpot);

        vehicle.setCheckOut(new Date());
        Float charge = calculateCharge(vehicle);
        vehicle.setCharge(charge);
        vehicleRepository.save(vehicle);
        return charge;
    }

    public void markSpotFull(ParkingSpot spot, Vehicle vehicle) {
        spot.setFree(false);
        spot.setVehicle(vehicle);
        parkingSpotRepository.save(spot);

        vehicle.setCheckIn(new Date());
        vehicleRepository.save(vehicle);
    }

    public Float calculateCharge(Vehicle vehicle) {
        VehicleType type = vehicle.getType();
        long diff = vehicle.getCheckOut().getTime() - vehicle.getCheckIn().getTime(); //in milli-sec
        long hour = diff / (1000 * 60 * 60);
        long hourRoundOff = diff % 3600000 == 0 ? hour : hour + 1; //if exact hour
        float charge = 0f;

        switch (type) {
            case BIKE:
                charge = hourRoundOff * 20;
                break;
            case CAR:
                charge = hourRoundOff * 50;
                break;
            case TRUCK:
                charge = hourRoundOff * 100;
                break;
        }
        return charge;
    }

    public ParkedVehicleSummaryResponse getParkedVehicleSummary() {
        List<ParkingSpot> occupiedSpots = parkingSpotRepository.findOccupiedSpots();

        List<ParkedVehicleSummaryItem> parkedVehicles = occupiedSpots.stream()
                .map(parkingSpot -> new ParkedVehicleSummaryItem(
                        parkingSpot.getId(),
                        parkingSpot.getType(),
                        parkingSpot.getVehicle().getId(),
                        parkingSpot.getVehicle().getVehicleNo(),
                        parkingSpot.getVehicle().getType()
                ))
                .collect(Collectors.toList());

        Map<String, Long> countsByVehicleType = parkedVehicles.stream()
                .collect(Collectors.groupingBy(
                        parkedVehicle -> parkedVehicle.getVehicleType().name(),
                        Collectors.counting()
                ));

        return new ParkedVehicleSummaryResponse(
                parkedVehicles.size(),
                countsByVehicleType,
                parkedVehicles
        );
    }

    private boolean hasAvailableSpot(VehicleType vehicleType) {
        if (vehicleType.equals(VehicleType.TRUCK)) {
            return !parkingSpotRepository.getSpots(ParkingSpotType.LARGE.toString(), true).isEmpty();
        }

        if (vehicleType.equals(VehicleType.CAR)) {
            return !parkingSpotRepository.getSpots(ParkingSpotType.COMPACT.toString(), true).isEmpty()
                    || !parkingSpotRepository.getSpots(ParkingSpotType.LARGE.toString(), true).isEmpty();
        }

        return !parkingSpotRepository.getSpots(ParkingSpotType.BIKE.toString(), true).isEmpty()
                || !parkingSpotRepository.getSpots(ParkingSpotType.COMPACT.toString(), true).isEmpty()
                || !parkingSpotRepository.getSpots(ParkingSpotType.LARGE.toString(), true).isEmpty();
    }

    private void assignParkingSpot(Vehicle vehicle) {
        VehicleType vehicleType = vehicle.getType();
        ParkingSpot parkingSpot = findAvailableSpot(vehicleType);
        if (parkingSpot == null) {
            throw new ResponseStatusException(CONFLICT, "No parking spot available for vehicle type: " + vehicleType);
        }
        markSpotFull(parkingSpot, vehicle);
    }

    private ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        if (vehicleType.equals(VehicleType.TRUCK)) {
            return getFirstAvailableSpot(ParkingSpotType.LARGE);
        }

        if (vehicleType.equals(VehicleType.CAR)) {
            ParkingSpot compactSpot = getFirstAvailableSpot(ParkingSpotType.COMPACT);
            return compactSpot != null ? compactSpot : getFirstAvailableSpot(ParkingSpotType.LARGE);
        }

        ParkingSpot bikeSpot = getFirstAvailableSpot(ParkingSpotType.BIKE);
        if (bikeSpot != null) {
            return bikeSpot;
        }

        ParkingSpot compactSpot = getFirstAvailableSpot(ParkingSpotType.COMPACT);
        return compactSpot != null ? compactSpot : getFirstAvailableSpot(ParkingSpotType.LARGE);
    }

    private ParkingSpot getFirstAvailableSpot(ParkingSpotType parkingSpotType) {
        List<ParkingSpot> freeSpots = parkingSpotRepository.getSpots(parkingSpotType.toString(), true);
        return freeSpots.isEmpty() ? null : freeSpots.get(0);
    }


}
