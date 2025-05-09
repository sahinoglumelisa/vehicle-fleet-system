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


    @Column(name = "company_id", nullable = false)
    private Integer companyId; // You can map this as an object if you have a Company entity

    @Id
    @Column(name = "plate_number", nullable = false, unique = true)
    private String plateNumber;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_type", nullable = false)
    private OwnershipType ownershipType;

    @Column(name = "current_odometer", precision = 10, scale = 2)
    private BigDecimal currentOdometer;

    @Column(name = "previous_month_odometer", precision = 10, scale = 2)
    private BigDecimal previousMonthOdometer;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Integer getCompanyId() {
        return companyId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Integer getYear() {
        return year;
    }

    public VehicleType getType() {
        return type;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public OwnershipType getOwnershipType() {
        return ownershipType;
    }

    public BigDecimal getCurrentOdometer() {
        return currentOdometer;
    }

    public BigDecimal getPreviousMonthOdometer() {
        return previousMonthOdometer;
    }

    public Boolean getActive() {
        return isActive;
    }
}
