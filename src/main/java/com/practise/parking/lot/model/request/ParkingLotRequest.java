package com.practise.parking.lot.model.request;

import com.practise.parking.lot.model.enums.Floor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Parking lot setup request for a single floor.")
public class ParkingLotRequest {
    @Schema(description = "Number of bike spots to create.", example = "10")
    int bike;
    @Schema(description = "Number of compact spots to create.", example = "20")
    int compact;
    @Schema(description = "Number of large spots to create.", example = "5")
    int large;
    @Schema(description = "Floor where these spots exist.", example = "GROUND")
    Floor floor;
}
