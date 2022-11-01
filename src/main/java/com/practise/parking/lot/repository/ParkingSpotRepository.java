package com.practise.parking.lot.repository;

import com.practise.parking.lot.model.ParkingSpot;
import com.practise.parking.lot.model.enums.ParkingSpotType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends CrudRepository<ParkingSpot, Long> {

    List<ParkingSpot> findAllByTypeAndFree(ParkingSpotType type, Boolean free);
    ParkingSpot findByVehicleId(Long vehicleId);
}
