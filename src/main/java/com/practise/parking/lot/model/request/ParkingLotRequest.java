package com.practise.parking.lot.model.request;

import com.practise.parking.lot.model.enums.Floor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotRequest {
    int bike;
    int compact;
    int large;
    Floor floor;
}
