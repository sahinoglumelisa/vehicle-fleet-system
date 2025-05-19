package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "vehicle_usage")
public class VehicleUsage {

    public VehicleUsage(Integer usageId, Integer vehicle, Integer driver, LocalDate startDate, LocalDate endDate, Double startOdometer, Double endOdometer, String purpose, Boolean isVerified) {
        this.usageId = usageId;
        this.vehicle = vehicle;
        this.driver = driver;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startOdometer = startOdometer;
        this.endOdometer = endOdometer;
        this.purpose = purpose;
        this.isVerified = isVerified;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Integer usageId;

    @Column(name = "vehicle_id")
    private Integer vehicle;

    @Column(name = "driver_id")
    private Integer driver;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private LocalDate startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private LocalDate endDate;

    @Column(name = "start_odometer")
    private Double startOdometer;

    @Column(name = "end_odometer")
    private Double endOdometer;

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "is_verified")
    private Boolean isVerified;

    public VehicleUsage() {

    }

    public Double getDrivenKm() {
        return this.endOdometer - this.startOdometer;
    }

    public Integer getUsageId() {
        return usageId;
    }

    public Integer getVehicle() {
        return vehicle;
    }

    public Integer getDriver() {
        return driver;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Double getStartOdometer() {
        return startOdometer;
    }

    public Double getEndOdometer() {
        return endOdometer;
    }

    public String getPurpose() {
        return purpose;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setUsageId(Integer usageId) {
        this.usageId = usageId;
    }

    public void setVehicle(Integer vehicle) {
        this.vehicle = vehicle;
    }

    public void setDriver(Integer driver) {
        this.driver = driver;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartOdometer(Double startOdometer) {
        this.startOdometer = startOdometer;
    }

    public void setEndOdometer(Double endOdometer) {
        this.endOdometer = endOdometer;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }
}