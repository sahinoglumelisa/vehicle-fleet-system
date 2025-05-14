package com.group13.fleet.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "vehicles")
@ToString
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "company_id")
    private Integer customer;

    @Column(name = "plate_number", unique = true, nullable = false, length = 20)
    private String plateNumber;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "model", length = 50)
    private String model;

    @Column(name = "year")
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private VehicleType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VehicleStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "ownership_type")
    private OwnershipType ownershipType;

    @Column(name = "current_odometer")
    private Double currentOdometer;

    @Column(name = "previous_month_odometer")
    private Double previousMonthOdometer;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @OneToMany(mappedBy = "vehicle")
    private List<Service> services;

    @OneToMany(mappedBy = "vehicle")
    private List<FuelConsumption> fuelConsumptions;

    @OneToMany(mappedBy = "vehicle")
    private List<VehicleUsage> vehicleUsages;
}