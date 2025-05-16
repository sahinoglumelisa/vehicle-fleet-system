package com.group13.fleet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@Table(name = "customers")
@ToString
@Getter
public class Customer {
    @Id
    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @OneToMany(mappedBy = "customer")
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "customer")
    private List<ExpenseTracker> expenseTrackers;

    public Integer getCompanyId() {
        return companyId;
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

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<ExpenseTracker> getExpenseTrackers() {
        return expenseTrackers;
    }
}