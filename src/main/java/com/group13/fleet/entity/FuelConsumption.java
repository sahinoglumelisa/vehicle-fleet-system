package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "fuel_consumption")
public class FuelConsumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fuel_id")
    private Long fuelId;

    @Column(name = "vehicle_id")
    private Integer vehicle;

    @Column(name = "driver_id")
    private Integer driver;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "liters")
    private Double liters;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "odometer_reading")
    private Double odometerReading;
}