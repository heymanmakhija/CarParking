package com.practise.parking.lot.model;

import com.practise.parking.lot.model.enums.ParkingSpotType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Enumerated(EnumType.STRING)
    ParkingSpotType type;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    Vehicle vehicle;
    Boolean free = true;
    @CreatedDate
    Date dateCreated;
    @LastModifiedDate
    Date lastUpdated;


    @PrePersist
    void prePersist() {
        this.dateCreated = new Date();
    }

    @PreUpdate
    void preUpdate() {
        this.lastUpdated = new Date();
    }
}
