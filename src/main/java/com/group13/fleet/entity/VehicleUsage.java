package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "vehicle_usage")
public class VehicleUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

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

    public Double getDrivenKm() {
        return this.endOdometer - this.startOdometer;
    }
}