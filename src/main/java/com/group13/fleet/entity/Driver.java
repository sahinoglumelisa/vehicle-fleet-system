package com.group13.fleet.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    @Id
    @JsonProperty
    @Column(name = "driver_id")
    private Long id;

    @JsonProperty
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @JsonProperty
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @JsonProperty
    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;

    @JsonProperty
    @Column(name = "is_active")
    private Boolean isActive = true;

}

