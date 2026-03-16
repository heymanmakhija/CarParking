package com.practise.parking.lot.model.response;

import com.practise.parking.lot.model.enums.ParkingSpotType;
import com.practise.parking.lot.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParkedVehicleSummaryItem {
    private Long parkingSpotId;
    private ParkingSpotType parkingSpotType;
    private Long vehicleId;
    private String vehicleNo;
    private VehicleType vehicleType;
}
