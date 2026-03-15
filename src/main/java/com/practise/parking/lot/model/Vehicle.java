package com.practise.parking.lot.model;

import com.practise.parking.lot.model.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Vehicle entering the parking lot.")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Generated vehicle id.", accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    Long id;
    @Schema(description = "Vehicle registration number.", example = "DL01AB1234")
    String vehicleNo;
    @Enumerated(EnumType.STRING)
    @Schema(description = "Vehicle category.", example = "CAR")
    VehicleType type;
    @Schema(description = "Accumulated parking charge.", accessMode = Schema.AccessMode.READ_ONLY, example = "40.0")
    Float charge = 0.0f;
    @CreatedDate
    @Schema(description = "Vehicle check-in time.", accessMode = Schema.AccessMode.READ_ONLY)
    Date checkIn;
    @Schema(description = "Vehicle check-out time.", accessMode = Schema.AccessMode.READ_ONLY)
    Date checkOut;
    @LastModifiedDate
    @Schema(description = "Last update time for the vehicle record.", accessMode = Schema.AccessMode.READ_ONLY)
    Date modifiedDate;

    @PrePersist
    void prePersist() {
        this.checkIn = new Date();
    }

    @PreUpdate
    void preUpdate() {
        this.modifiedDate = new Date();
    }

}
