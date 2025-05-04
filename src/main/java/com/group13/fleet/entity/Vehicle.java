package com.group13.fleet.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicle")
@Getter
@Setter
public class Vehicle {


    @JsonProperty
    @Column(name = "company_id", nullable = false)
    private Integer companyId; // You can map this as an object if you have a Company entity

    @Id
    @JsonProperty
    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    @JsonProperty
    @Column(name = "brand")
    private String brand;

    @JsonProperty
    @Column(name = "model")
    private String model;

    @JsonProperty
    @Column(name = "year")
    private Integer year;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VehicleType type;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_type", nullable = false)
    private OwnershipType ownershipType;

    @JsonProperty
    @Column(name = "current_odometer", precision = 10, scale = 2)
    private BigDecimal currentOdometer;

    @JsonProperty
    @Column(name = "previous_month_odometer", precision = 10, scale = 2)
    private BigDecimal previousMonthOdometer;

    @JsonProperty
    @Column(name = "is_active")
    private Boolean isActive = true;
}
