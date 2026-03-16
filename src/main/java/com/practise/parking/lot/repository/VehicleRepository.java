package com.practise.parking.lot.repository;

import com.practise.parking.lot.model.Vehicle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends CrudRepository<Vehicle, Long> {

    boolean existsByVehicleNo(String vehicleNo);

    Vehicle findByVehicleNo(String vehicleNo);

}
