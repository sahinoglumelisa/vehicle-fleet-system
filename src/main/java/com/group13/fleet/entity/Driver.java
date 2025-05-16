package com.group13.fleet.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "license_expiry_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date licenseExpiryDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "driver")
    private List<FuelConsumption> fuelConsumptions;

    @OneToMany(mappedBy = "driver")
    private List<VehicleUsage> vehicleUsages;

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public Date getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public Boolean getActive() {
        return isActive;
    }

    public List<FuelConsumption> getFuelConsumptions() {
        return fuelConsumptions;
    }

    public List<VehicleUsage> getVehicleUsages() {
        return vehicleUsages;
    }
}