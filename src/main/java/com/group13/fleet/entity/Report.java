package com.group13.fleet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "generated_date")
    @Temporal(TemporalType.DATE)
    private Date generatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ReportType type;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "data")
    private String data;

    @ManyToMany(mappedBy = "reports")
    private List<ExpenseTracker> expenseTrackers;
}