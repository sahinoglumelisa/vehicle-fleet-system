package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_usage")
public class VehicleUsage {

    @Id
    @Column(name = "usage_id")
    @JsonProperty
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_plate", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @JsonProperty
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @JsonProperty
    @Column(name = "end_date")
    private LocalDate endDate;

    @JsonProperty
    @Column(name = "start_odometer")
    private BigDecimal startOdometer;
}