package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "service")
@Data
public class Service {

    @Id
    @JsonProperty
    @Column(name = "service_id")
    private Long id;

    @JsonProperty
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @JsonProperty
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @JsonProperty
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JsonProperty
    @Column(name = "cost")
    private BigDecimal cost;

    @JsonProperty
    @Column(name = "is_covered_by_insurance", nullable = false)
    private Boolean isCoveredByInsurance = false;

    @JsonProperty
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_entered_by")
    private User dataEnteredBy;

    @JsonProperty
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
