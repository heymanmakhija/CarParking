package com.practise.parking.lot.repository;

import com.practise.parking.lot.model.ParkingLot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends CrudRepository<ParkingLot, Long> {

}
