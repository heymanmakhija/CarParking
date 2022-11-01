package com.practise.parking.lot.repository;

import com.practise.parking.lot.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    //    List<ParkingSpot> findAllByTypeAndFree(ParkingSpotType type, Boolean free);
    @Query(value = "SELECT * from parking_spot where type=:type and free=:free", nativeQuery = true)
    List<ParkingSpot> getSpots(String type, Boolean free);

    ParkingSpot findByVehicleId(Long vehicleId);
}
