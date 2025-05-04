package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "company")
@Data
public class Company {
    @Id
    @Column(name = "company_id")
    @JsonProperty
    private int id;

    @JsonProperty
    @Column(name = "name")
    private String name;

    @JsonProperty
    @Column(name = "address")
    private String address;

    @JsonProperty
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonProperty
    @Column(name = "tax_number")
    private String taxNumber;

}
