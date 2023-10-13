package com.practise.parking.lot.model;

import com.practise.parking.lot.model.enums.VehicleType;
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
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String vehicleNo;
    @Enumerated(EnumType.STRING)
    VehicleType type;
    Float charge = 0.0f;
    @CreatedDate
    Date checkIn;
    Date checkOut;
    @LastModifiedDate
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
