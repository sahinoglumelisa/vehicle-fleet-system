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
    private Integer vehicleId;

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

    @Column(name = "driver_id")
    private Integer driver;

    @OneToMany(mappedBy = "vehicle")
    private List<Service> services;

    @OneToMany(mappedBy = "vehicle")
    private List<FuelConsumption> fuelConsumptions;

    @OneToMany(mappedBy = "vehicle")
    private List<VehicleUsage> vehicleUsages;

    public Integer getVehicleId() {
        return vehicleId;
    }

    public Integer getCustomer() {
        return customer;
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

    public Double getCurrentOdometer() {
        return currentOdometer;
    }

    public Double getPreviousMonthOdometer() {
        return previousMonthOdometer;
    }

    public Integer getDriver() {
        return driver;
    }

    public List<Service> getServices() {
        return services;
    }

    public List<FuelConsumption> getFuelConsumptions() {
        return fuelConsumptions;
    }

    public List<VehicleUsage> getVehicleUsages() {
        return vehicleUsages;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setCustomer(Integer customer) {
        this.customer = customer;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public void setOwnershipType(OwnershipType ownershipType) {
        this.ownershipType = ownershipType;
    }

    public void setCurrentOdometer(Double currentOdometer) {
        this.currentOdometer = currentOdometer;
    }

    public void setPreviousMonthOdometer(Double previousMonthOdometer) {
        this.previousMonthOdometer = previousMonthOdometer;
    }

    public void setDriver(Integer driver) {
        this.driver = driver;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setFuelConsumptions(List<FuelConsumption> fuelConsumptions) {
        this.fuelConsumptions = fuelConsumptions;
    }

    public void setVehicleUsages(List<VehicleUsage> vehicleUsages) {
        this.vehicleUsages = vehicleUsages;
    }
}