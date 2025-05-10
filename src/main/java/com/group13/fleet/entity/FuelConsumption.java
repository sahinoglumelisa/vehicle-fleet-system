package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_consumption")
@Getter
@Setter
public class FuelConsumption {

    @Id
    @JsonProperty
    @Column(name = "fuel_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @JsonProperty
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @JsonProperty
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @JsonProperty
    @Column(name = "liters", nullable = false)
    private BigDecimal liters;

    @JsonProperty
    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @JsonProperty
    @Column(name = "odometer_reading", nullable = false)
    private BigDecimal odometerReading;

    @JsonProperty
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_entered_by")
    private User dataEnteredBy;

    @JsonProperty
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
