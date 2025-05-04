package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "users")
public class User {
    @Id
    @JsonProperty
    private int userId;

    @JsonProperty
    @Column(name = "username")
    private String username;

    @JsonProperty
    @Column(name = "password")
    private String password;

    @JsonProperty
    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @JsonProperty
    private Role role;

    @ManyToOne
    @JsonProperty
    @JoinColumn(name = "company_id", foreignKey = @ForeignKey(name = "fk_users_company"))
    private Company companyId;
}
