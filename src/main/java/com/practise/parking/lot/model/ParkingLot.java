package com.practise.parking.lot.model;

import com.practise.parking.lot.model.enums.Floor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToMany(cascade = CascadeType.ALL)
    List<ParkingSpot> spots;
    @Enumerated(EnumType.STRING)
    Floor floor;
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
