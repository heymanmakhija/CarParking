package com.practise.parking.lot.service;

import com.practise.parking.lot.model.ParkingLot;
import com.practise.parking.lot.model.ParkingSpot;
import com.practise.parking.lot.model.Vehicle;
import com.practise.parking.lot.model.enums.ParkingSpotType;
import com.practise.parking.lot.model.enums.VehicleType;
import com.practise.parking.lot.model.request.ParkingLotRequest;
import com.practise.parking.lot.repository.ParkingLotRepository;
import com.practise.parking.lot.repository.ParkingSpotRepository;
import com.practise.parking.lot.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        Vehicle newVehicle = vehicleRepository.save(vehicle);
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
        parkingLot.setSlots(parkingSpotList);
        parkingLot.setFloor(parkingLotRequest.getFloor());
        parkingLotRepository.save(parkingLot);
    }

    public void parkVehicle(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId).get();
        VehicleType vehicleType = vehicle.getType();

        if (vehicleType.equals(VehicleType.TRUCK)) {
            List<ParkingSpot> freeTruckSpots = parkingSpotRepository.findAllByTypeAndFree(ParkingSpotType.LARGE, true);
            if (freeTruckSpots.size() > 0) {
                ParkingSpot spot = freeTruckSpots.get(0);
                markSpotFull(spot, vehicle);
            }
        } else if (vehicleType.equals(VehicleType.CAR)) {
            List<ParkingSpot> freeCarSpots = parkingSpotRepository.findAllByTypeAndFree(ParkingSpotType.COMPACT, true);
            if (freeCarSpots.size() > 0) {
                ParkingSpot spot = freeCarSpots.get(0);
                markSpotFull(spot, vehicle);
            } else {
                List<ParkingSpot> freeTruckSpots = parkingSpotRepository.findAllByTypeAndFree(ParkingSpotType.LARGE, true);
                if (freeTruckSpots.size() > 0) {
                    ParkingSpot spot = freeTruckSpots.get(0);
                    markSpotFull(spot, vehicle);
                }
            }
        } else {
            List<ParkingSpot> freeBikeSpots = parkingSpotRepository.findAllByTypeAndFree(ParkingSpotType.BIKE, true);
            if (freeBikeSpots.size() > 0) {
                ParkingSpot spot = freeBikeSpots.get(0);
                markSpotFull(spot, vehicle);
            } else {
                List<ParkingSpot> freeCarSpots = parkingSpotRepository.findAllByTypeAndFree(ParkingSpotType.COMPACT, true);
                if (freeCarSpots.size() > 0) {
                    ParkingSpot spot = freeCarSpots.get(0);
                    spot.setFree(false);
                    spot.setVehicle(vehicle);
                    parkingSpotRepository.save(spot);
                } else {
                    List<ParkingSpot> freeTruckSpots = parkingSpotRepository.findAllByTypeAndFree(ParkingSpotType.LARGE, true);
                    if (freeTruckSpots.size() > 0) {
                        ParkingSpot spot = freeTruckSpots.get(0);
                        markSpotFull(spot, vehicle);
                    }
                }
            }
        }

    }

    public Float unParkVehicle(Long vehicleId) {
        ParkingSpot parkingSpot = parkingSpotRepository.findByVehicleId(vehicleId);
        parkingSpot.setFree(true);
        parkingSpot.setVehicle(null);
        parkingSpotRepository.save(parkingSpot);

        Vehicle vehicle = vehicleRepository.findById(vehicleId).get();
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

    Float calculateCharge(Vehicle vehicle) {
        VehicleType type = vehicle.getType();
        long diff = vehicle.getCheckOut().getTime() - vehicle.getCheckIn().getTime();
        long hour = diff / (1000 * 60 * 60);
        long hourRoundOff = diff % 3600000 == 0 ? hour : hour + 1;
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


}
