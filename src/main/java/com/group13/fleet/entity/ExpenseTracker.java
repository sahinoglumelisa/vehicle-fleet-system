package com.group13.fleet.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "expense_tracker")
@Getter
@Setter
public class ExpenseTracker {

    @Id
    @JsonProperty
    @Column(name = "id")
    private Long id;

    @JsonProperty
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @JsonProperty
    @Enumerated(EnumType.STRING)
    @Column(name = "expense_type", nullable = false)
    private ExpenseType expenseType;

}
