package com.practise.parking.lot.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ParkedVehicleSummaryResponse {
    private int totalParkedVehicles;
    private Map<String, Long> countsByVehicleType;
    private List<ParkedVehicleSummaryItem> parkedVehicles;
}
