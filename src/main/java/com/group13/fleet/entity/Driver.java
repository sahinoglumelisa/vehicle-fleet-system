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
    private Integer userId;

    @Column(name = "company_id")
    private Integer companyId;

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
    private LocalDate licenseExpiryDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "driver")
    private List<FuelConsumption> fuelConsumptions;

    @OneToMany(mappedBy = "driver")
    private List<VehicleUsage> vehicleUsages;

    public Integer getUserId() {
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

    public LocalDate getLicenseExpiryDate() {
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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setFuelConsumptions(List<FuelConsumption> fuelConsumptions) {
        this.fuelConsumptions = fuelConsumptions;
    }

    public void setVehicleUsages(List<VehicleUsage> vehicleUsages) {
        this.vehicleUsages = vehicleUsages;
    }
}